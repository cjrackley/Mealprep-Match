package com.CSC340.MealPrep_Match.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Customer;
import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.entity.Provider;
import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.entity.Review;
import com.CSC340.MealPrep_Match.entity.Save;
import com.CSC340.MealPrep_Match.entity.Subscription;
import com.CSC340.MealPrep_Match.repository.CustomerRepository;
import com.CSC340.MealPrep_Match.repository.MealkitRepository;
import com.CSC340.MealPrep_Match.repository.MealplanRepository;
import com.CSC340.MealPrep_Match.repository.ProviderRepository;
import com.CSC340.MealPrep_Match.repository.RecipeRepository;
import com.CSC340.MealPrep_Match.repository.ReviewRepository;
import com.CSC340.MealPrep_Match.repository.SaveRepository;
import com.CSC340.MealPrep_Match.repository.SubscriptionRepository;

/**
 * Integration tests for {@link ContentDeletionService} against a real database.
 * <p>
 * Nothing in the entity mappings cascades, so every delete path hand-clears its
 * dependents — join-table rows in {@code mealplan_recipes} / {@code mealkit_recipes},
 * plus saves, reviews and subscriptions. That hand-rolled cleanup either works or
 * throws a foreign key violation, which is precisely what these tests pin down.
 * <p>
 * <b>Deliberately not {@code @Transactional}.</b> A rollback-only test transaction
 * would not reproduce the flush and constraint-check ordering that production sees,
 * and constraint ordering is the whole point here. Each service call therefore
 * commits for real, and {@link #cleanUp()} removes only the rows this test created —
 * never a blanket {@code deleteAll()}, since this may run against a database that
 * holds real data.
 * <p>
 * Requires a reachable database via {@code SPRING_DATASOURCE_URL}. CI supplies an
 * empty Postgres container; see {@code .github/workflows/build.yml}.
 */
@SpringBootTest
@DisplayName("ContentDeletionService (integration)")
class ContentDeletionServiceTest {

    @Autowired
    private ContentDeletionService contentDeletionService;

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private MealplanService mealplanService;
    @Autowired
    private MealkitService mealkitService;
    @Autowired
    private SaveService saveService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private MealplanRepository mealplanRepository;
    @Autowired
    private MealkitRepository mealkitRepository;
    @Autowired
    private SaveRepository saveRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // Cleanup ledger, in creation order. Torn down in reverse dependency order.
    private final List<Long> createdMealplanIds = new ArrayList<>();
    private final List<Long> createdMealkitIds = new ArrayList<>();
    private final List<Long> createdRecipeIds = new ArrayList<>();
    private final List<Long> createdProviderIds = new ArrayList<>();
    private final List<Long> createdCustomerIds = new ArrayList<>();

    @Test
    @DisplayName("deleting a recipe unlinks it from plans and kits and removes its saves and reviews")
    void deletingRecipeClearsEveryReferenceToIt() {
        Provider provider = newProvider();
        Customer customer = newCustomer();

        // Two recipes, so we can prove the plan and kit survive one recipe shorter
        // rather than simply being emptied.
        Recipe doomed = newRecipe(provider, "Doomed Recipe");
        Recipe survivor = newRecipe(provider, "Surviving Recipe");

        Mealplan mealplan = newMealplan(provider, List.of(doomed, survivor));
        Mealkit mealkit = newMealkit(provider, List.of(doomed, survivor));

        // Customer activity pointing at the recipe that is about to go.
        saveRecipe(customer, doomed);
        reviewRecipe(customer, doomed);
        // ...and activity on the mealplan, which must be left untouched.
        saveMealplan(customer, mealplan);

        contentDeletionService.deleteRecipe(doomed.getId(), provider.getId());

        assertFalse(recipeRepository.existsById(doomed.getId()), "the recipe itself should be gone");
        assertTrue(recipeRepository.existsById(survivor.getId()), "the other recipe must not be touched");

        // Join rows: querying by recipe id avoids loading a lazy collection outside a session.
        assertTrue(mealplanRepository.findByRecipes_Id(doomed.getId()).isEmpty(),
                "mealplan_recipes should no longer link the deleted recipe");
        assertTrue(mealkitRepository.findByRecipes_Id(doomed.getId()).isEmpty(),
                "mealkit_recipes should no longer link the deleted recipe");

        assertTrue(mealplanRepository.existsById(mealplan.getId()), "the mealplan must survive");
        assertTrue(mealkitRepository.existsById(mealkit.getId()), "the mealkit must survive");
        assertEquals(1, mealplanRepository.findByRecipes_Id(survivor.getId()).size(),
                "the mealplan should still link the surviving recipe");
        assertEquals(1, mealkitRepository.findByRecipes_Id(survivor.getId()).size(),
                "the mealkit should still link the surviving recipe");

        assertTrue(saveRepository.findByRecipe_Id(doomed.getId()).isEmpty(), "its saves should be gone");
        assertTrue(reviewRepository.findByRecipe_Id(doomed.getId()).isEmpty(), "its reviews should be gone");

        assertEquals(1, saveRepository.findByMealplan_Id(mealplan.getId()).size(),
                "the save on the mealplan is unrelated and must survive");
    }

    @Test
    @DisplayName("deleting a mealplan removes its saves and reviews but keeps its recipes")
    void deletingMealplanKeepsItsRecipes() {
        Provider provider = newProvider();
        Customer customer = newCustomer();

        Recipe recipe = newRecipe(provider, "Recipe In A Plan");
        Mealplan mealplan = newMealplan(provider, List.of(recipe));

        saveMealplan(customer, mealplan);
        reviewMealplan(customer, mealplan);

        contentDeletionService.deleteMealplan(mealplan.getId(), provider.getId());

        assertFalse(mealplanRepository.existsById(mealplan.getId()), "the mealplan should be gone");
        assertTrue(saveRepository.findByMealplan_Id(mealplan.getId()).isEmpty(), "its saves should be gone");
        assertTrue(reviewRepository.findByMealplan_Id(mealplan.getId()).isEmpty(), "its reviews should be gone");

        assertTrue(recipeRepository.existsById(recipe.getId()),
                "recipes belong to the provider independently and must outlive the plan");
    }

    @Test
    @DisplayName("deleting a mealkit cancels its subscriptions and removes its reviews")
    void deletingMealkitCancelsSubscriptions() {
        Provider provider = newProvider();
        Customer customer = newCustomer();

        Recipe recipe = newRecipe(provider, "Recipe In A Kit");
        Mealkit mealkit = newMealkit(provider, List.of(recipe));

        subscribe(customer, mealkit);
        // Mealkit reviews are subscription-gated, so this only works after subscribing.
        reviewMealkit(customer, mealkit);

        contentDeletionService.deleteMealkit(mealkit.getId(), provider.getId());

        assertFalse(mealkitRepository.existsById(mealkit.getId()), "the mealkit should be gone");
        assertTrue(subscriptionRepository.findByMealkit_Id(mealkit.getId()).isEmpty(),
                "live subscriptions should be cancelled with it");
        assertTrue(reviewRepository.findByMealkit_Id(mealkit.getId()).isEmpty(), "its reviews should be gone");

        assertTrue(recipeRepository.existsById(recipe.getId()), "its recipes must survive");
    }

    @Test
    @DisplayName("a provider cannot delete another provider's content")
    void deletingSomebodyElsesContentIsForbidden() {
        Provider owner = newProvider();
        Provider intruder = newProvider();

        Recipe recipe = newRecipe(owner, "Not Yours");
        Mealplan mealplan = newMealplan(owner, List.of(recipe));
        Mealkit mealkit = newMealkit(owner, List.of(recipe));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(
                () -> contentDeletionService.deleteRecipe(recipe.getId(), intruder.getId())));
        assertEquals(HttpStatus.FORBIDDEN, statusOf(
                () -> contentDeletionService.deleteMealplan(mealplan.getId(), intruder.getId())));
        assertEquals(HttpStatus.FORBIDDEN, statusOf(
                () -> contentDeletionService.deleteMealkit(mealkit.getId(), intruder.getId())));

        assertTrue(recipeRepository.existsById(recipe.getId()), "a refused delete must not remove anything");
        assertTrue(mealplanRepository.existsById(mealplan.getId()), "a refused delete must not remove anything");
        assertTrue(mealkitRepository.existsById(mealkit.getId()), "a refused delete must not remove anything");
    }

    @Test
    @DisplayName("deleting content that does not exist is a 404")
    void deletingMissingContentIsNotFound() {
        Provider provider = newProvider();
        long missing = 999_999_999L;

        assertEquals(HttpStatus.NOT_FOUND, statusOf(
                () -> contentDeletionService.deleteRecipe(missing, provider.getId())));
        assertEquals(HttpStatus.NOT_FOUND, statusOf(
                () -> contentDeletionService.deleteMealplan(missing, provider.getId())));
        assertEquals(HttpStatus.NOT_FOUND, statusOf(
                () -> contentDeletionService.deleteMealkit(missing, provider.getId())));
    }

    // ---------- fixtures ----------

    /** Unique per run, so repeated runs never collide on the unique email columns. */
    private String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    private Provider newProvider() {
        Provider provider = new Provider();
        provider.setName("Test Provider");
        provider.setEmail(unique("provider") + "@example.test");
        provider.setPassword("password");
        Provider created = providerService.create(provider);
        createdProviderIds.add(created.getId());
        return created;
    }

    private Customer newCustomer() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail(unique("customer") + "@example.test");
        customer.setPassword("password");
        Customer created = customerService.create(customer);
        createdCustomerIds.add(created.getId());
        return created;
    }

    private Recipe newRecipe(Provider provider, String title) {
        Recipe recipe = new Recipe();
        recipe.setProvider(provider);
        recipe.setTitle(title);
        recipe.setInstructions("Cook it.");
        recipe.setIngredients(new ArrayList<>(List.of("salt")));
        recipe.setTags(new ArrayList<>(List.of("test")));
        Recipe created = recipeService.create(recipe);
        createdRecipeIds.add(created.getId());
        return created;
    }

    private Mealplan newMealplan(Provider provider, List<Recipe> recipes) {
        Mealplan mealplan = new Mealplan();
        mealplan.setProvider(provider);
        mealplan.setTitle("Test Mealplan");
        mealplan.setDuration("7 days");
        mealplan.setDescription("A plan.");
        mealplan.setSchedule("Mon: something");
        mealplan.setRecipes(new ArrayList<>(recipes));
        Mealplan created = mealplanService.create(mealplan);
        createdMealplanIds.add(created.getId());
        return created;
    }

    private Mealkit newMealkit(Provider provider, List<Recipe> recipes) {
        Mealkit mealkit = new Mealkit();
        mealkit.setProvider(provider);
        mealkit.setTitle("Test Mealkit");
        mealkit.setDuration("1 week");
        mealkit.setDescription("A kit.");
        mealkit.setPrice(49.99);
        // Curated separately from the recipes on purpose - see the locked domain model.
        mealkit.setIngredients(new ArrayList<>(List.of("400g chicken thigh")));
        mealkit.setRecipes(new ArrayList<>(recipes));
        Mealkit created = mealkitService.create(mealkit);
        createdMealkitIds.add(created.getId());
        return created;
    }

    private void saveRecipe(Customer customer, Recipe recipe) {
        Save save = new Save();
        save.setCustomer(stubCustomer(customer));
        save.setRecipe(stubRecipe(recipe));
        saveService.create(save);
    }

    private void saveMealplan(Customer customer, Mealplan mealplan) {
        Save save = new Save();
        save.setCustomer(stubCustomer(customer));
        save.setMealplan(stubMealplan(mealplan));
        saveService.create(save);
    }

    private void reviewRecipe(Customer customer, Recipe recipe) {
        Review review = new Review();
        review.setCustomer(stubCustomer(customer));
        review.setRecipe(stubRecipe(recipe));
        review.setRating(5);
        review.setComment("Great.");
        reviewService.create(review);
    }

    private void reviewMealplan(Customer customer, Mealplan mealplan) {
        Review review = new Review();
        review.setCustomer(stubCustomer(customer));
        review.setMealplan(stubMealplan(mealplan));
        review.setRating(4);
        review.setComment("Solid plan.");
        reviewService.create(review);
    }

    private void reviewMealkit(Customer customer, Mealkit mealkit) {
        Review review = new Review();
        review.setCustomer(stubCustomer(customer));
        review.setMealkit(stubMealkit(mealkit));
        review.setRating(3);
        review.setComment("Decent kit.");
        reviewService.create(review);
    }

    private void subscribe(Customer customer, Mealkit mealkit) {
        Subscription subscription = new Subscription();
        subscription.setCustomer(stubCustomer(customer));
        subscription.setMealkit(stubMealkit(mealkit));
        subscriptionService.create(subscription);
    }

    // The services take id-only stubs and resolve them, exactly as the UI controllers do.

    private Customer stubCustomer(Customer customer) {
        Customer stub = new Customer();
        stub.setId(customer.getId());
        return stub;
    }

    private Recipe stubRecipe(Recipe recipe) {
        Recipe stub = new Recipe();
        stub.setId(recipe.getId());
        return stub;
    }

    private Mealplan stubMealplan(Mealplan mealplan) {
        Mealplan stub = new Mealplan();
        stub.setId(mealplan.getId());
        return stub;
    }

    private Mealkit stubMealkit(Mealkit mealkit) {
        Mealkit stub = new Mealkit();
        stub.setId(mealkit.getId());
        return stub;
    }

    private HttpStatus statusOf(Runnable call) {
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, call::run);
        return HttpStatus.valueOf(thrown.getStatusCode().value());
    }

    // ---------- teardown ----------

    /**
     * Removes only what this test created, in reverse dependency order. Rows a test
     * already deleted are skipped, so this stays correct whether the test passed,
     * failed, or deleted things itself.
     */
    @AfterEach
    void cleanUp() {
        createdCustomerIds.forEach(customerId -> {
            reviewRepository.deleteAll(reviewRepository.findByCustomer_Id(customerId));
            saveRepository.deleteAll(saveRepository.findByCustomer_Id(customerId));
            subscriptionRepository.deleteAll(subscriptionRepository.findByCustomer_Id(customerId));
        });

        // Plans and kits own their join tables, so deleting them clears the join rows.
        createdMealplanIds.forEach(id -> deleteIfPresent(id, mealplanRepository::existsById,
                mealplanRepository::deleteById));
        createdMealkitIds.forEach(id -> deleteIfPresent(id, mealkitRepository::existsById,
                mealkitRepository::deleteById));
        createdRecipeIds.forEach(id -> deleteIfPresent(id, recipeRepository::existsById,
                recipeRepository::deleteById));
        createdCustomerIds.forEach(id -> deleteIfPresent(id, customerRepository::existsById,
                customerRepository::deleteById));
        createdProviderIds.forEach(id -> deleteIfPresent(id, providerRepository::existsById,
                providerRepository::deleteById));

        createdMealplanIds.clear();
        createdMealkitIds.clear();
        createdRecipeIds.clear();
        createdCustomerIds.clear();
        createdProviderIds.clear();
    }

    private void deleteIfPresent(Long id, java.util.function.Predicate<Long> exists,
            java.util.function.Consumer<Long> delete) {
        if (id != null && exists.test(id)) {
            delete.accept(id);
        }
    }
}
