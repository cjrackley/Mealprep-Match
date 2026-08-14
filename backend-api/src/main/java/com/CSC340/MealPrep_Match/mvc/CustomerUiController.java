package com.CSC340.MealPrep_Match.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.CSC340.MealPrep_Match.dto.ContentCard;
import com.CSC340.MealPrep_Match.dto.ReviewFormCard;
import com.CSC340.MealPrep_Match.entity.Customer;
import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.entity.Provider;
import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.entity.Review;
import com.CSC340.MealPrep_Match.entity.Save;
import com.CSC340.MealPrep_Match.entity.Subscription;
import com.CSC340.MealPrep_Match.service.CustomerService;
import com.CSC340.MealPrep_Match.service.MealkitService;
import com.CSC340.MealPrep_Match.service.MealplanService;
import com.CSC340.MealPrep_Match.service.ProviderService;
import com.CSC340.MealPrep_Match.service.RecipeService;
import com.CSC340.MealPrep_Match.service.ReviewService;
import com.CSC340.MealPrep_Match.service.SaveService;
import com.CSC340.MealPrep_Match.service.SubscriptionService;

@Controller
@RequestMapping("/customer")
public class CustomerUiController {

    private final CustomerService customerService;
    private final RecipeService recipeService;
    private final MealplanService mealplanService;
    private final MealkitService mealkitService;
    private final SubscriptionService subscriptionService;
    private final SaveService saveService;
    private final ReviewService reviewService;
    private final ProviderService providerService;

    public CustomerUiController(CustomerService customerService, RecipeService recipeService,
            MealplanService mealplanService, MealkitService mealkitService,
            SubscriptionService subscriptionService, SaveService saveService,
            ReviewService reviewService, ProviderService providerService) {
        this.customerService = customerService;
        this.recipeService = recipeService;
        this.mealplanService = mealplanService;
        this.mealkitService = mealkitService;
        this.subscriptionService = subscriptionService;
        this.saveService = saveService;
        this.reviewService = reviewService;
        this.providerService = providerService;
    }

    @GetMapping("/login")
    public String login() {
        return "customer/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email, @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.authenticate(email, password);
            return "redirect:/customer/" + customer.getId() + "/dashboard";
        } catch (ResponseStatusException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/customer/login";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "customer/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String name, @RequestParam String email,
            @RequestParam String password, RedirectAttributes redirectAttributes) {
        try {
            Customer toCreate = new Customer();
            toCreate.setName(name);
            toCreate.setEmail(email);
            toCreate.setPassword(password);
            Customer created = customerService.create(toCreate);
            return "redirect:/customer/" + created.getId() + "/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not register with that email.");
            return "redirect:/customer/register";
        }
    }

    @GetMapping("/{id}/dashboard")
    public String dashboard(@PathVariable Long id, Model model) {
        customerService.getById(id);
        ActionSets sets = actionSets(id);
        model.addAttribute("customerId", id);
        model.addAttribute("recipes", recipeService.getAll().stream()
                .map(recipe -> recipeCard(recipe, sets)).toList());
        model.addAttribute("mealplans", mealplanService.getAll().stream()
                .map(mealplan -> mealplanCard(mealplan, sets)).toList());
        model.addAttribute("mealkits", mealkitService.getAll().stream()
                .map(mealkit -> mealkitCard(mealkit, sets)).toList());
        return "customer/dashboard";
    }

    @PostMapping("/{id}/save/{type}/{contentId}")
    public String save(@PathVariable Long id, @PathVariable String type, @PathVariable Long contentId,
            @RequestParam(required = false) String redirect) {
        Save save = new Save();
        Customer customer = new Customer();
        customer.setId(id);
        save.setCustomer(customer);
        switch (type) {
            case "recipe" -> {
                Recipe recipe = new Recipe();
                recipe.setId(contentId);
                save.setRecipe(recipe);
            }
            case "mealplan" -> {
                Mealplan mealplan = new Mealplan();
                mealplan.setId(contentId);
                save.setMealplan(mealplan);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown save type: " + type);
        }
        saveService.create(save);
        return redirectTo(redirect, id, "dashboard");
    }

    @PostMapping("/{id}/unsave/{type}/{contentId}")
    public String unsave(@PathVariable Long id, @PathVariable String type, @PathVariable Long contentId,
            @RequestParam(required = false) String redirect) {
        switch (type) {
            case "recipe" -> saveService.deleteByCustomerAndRecipe(id, contentId);
            case "mealplan" -> saveService.deleteByCustomerAndMealplan(id, contentId);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown save type: " + type);
        }
        return redirectTo(redirect, id, "dashboard");
    }

    @PostMapping("/{id}/subscribe/{mealkitId}")
    public String subscribe(@PathVariable Long id, @PathVariable Long mealkitId,
            @RequestParam(required = false) String redirect) {
        Subscription subscription = new Subscription();
        Customer customer = new Customer();
        customer.setId(id);
        Mealkit mealkit = new Mealkit();
        mealkit.setId(mealkitId);
        subscription.setCustomer(customer);
        subscription.setMealkit(mealkit);
        subscriptionService.create(subscription);
        return redirectTo(redirect, id, "dashboard");
    }

    @PostMapping("/{id}/unsubscribe/{mealkitId}")
    public String unsubscribe(@PathVariable Long id, @PathVariable Long mealkitId,
            @RequestParam(required = false) String redirect) {
        subscriptionService.deleteByCustomerAndMealkit(id, mealkitId);
        return redirectTo(redirect, id, "dashboard");
    }

    @GetMapping("/{id}/saved")
    public String saved(@PathVariable Long id, Model model) {
        customerService.getById(id);
        ActionSets sets = actionSets(id);
        List<ContentCard> savedCards = saveService.getByCustomer(id).stream()
                .map(save -> save.getRecipe() != null
                        ? recipeCard(save.getRecipe(), sets)
                        : mealplanCard(save.getMealplan(), sets))
                .toList();
        model.addAttribute("customerId", id);
        model.addAttribute("savedCards", savedCards);
        return "customer/saved";
    }

    @GetMapping("/{id}/subscriptions")
    public String subscriptions(@PathVariable Long id, Model model) {
        customerService.getById(id);
        ActionSets sets = actionSets(id);
        List<ContentCard> subscriptions = subscriptionService.getByCustomer(id).stream()
                .map(subscription -> mealkitCard(subscription.getMealkit(), sets))
                .toList();
        model.addAttribute("customerId", id);
        model.addAttribute("subscriptions", subscriptions);
        return "customer/subscriptions";
    }

    @GetMapping("/{id}/reviews")
    public String reviews(@PathVariable Long id, Model model) {
        customerService.getById(id);
        ActionSets sets = actionSets(id);
        Map<String, Review> existingByKey = reviewService.getByCustomer(id).stream()
                .collect(Collectors.toMap(this::reviewKey, Function.identity(), (first, second) -> first));

        List<ReviewFormCard> reviewCards = new ArrayList<>();
        saveService.getByCustomer(id).forEach(save -> {
            ContentCard card = save.getRecipe() != null
                    ? recipeCard(save.getRecipe(), sets)
                    : mealplanCard(save.getMealplan(), sets);
            reviewCards.add(formCard(card, existingByKey));
        });
        subscriptionService.getByCustomer(id).forEach(subscription ->
                reviewCards.add(formCard(mealkitCard(subscription.getMealkit(), sets), existingByKey)));

        model.addAttribute("customerId", id);
        model.addAttribute("reviewCards", reviewCards);
        return "customer/reviews";
    }

    @PostMapping("/{id}/reviews/{type}/{contentId}")
    public String reviewSubmit(@PathVariable Long id, @PathVariable String type, @PathVariable Long contentId,
            @RequestParam Integer rating, @RequestParam(required = false) String comment) {
        String key = type + ":" + contentId;
        Review existing = reviewService.getByCustomer(id).stream()
                .filter(review -> reviewKey(review).equals(key))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            Review updates = new Review();
            updates.setRating(rating);
            updates.setComment(comment);
            reviewService.update(existing.getId(), updates);
        } else {
            Review review = new Review();
            Customer customer = new Customer();
            customer.setId(id);
            review.setCustomer(customer);
            switch (type) {
                case "recipe" -> {
                    Recipe recipe = new Recipe();
                    recipe.setId(contentId);
                    review.setRecipe(recipe);
                }
                case "mealplan" -> {
                    Mealplan mealplan = new Mealplan();
                    mealplan.setId(contentId);
                    review.setMealplan(mealplan);
                }
                case "mealkit" -> {
                    Mealkit mealkit = new Mealkit();
                    mealkit.setId(contentId);
                    review.setMealkit(mealkit);
                }
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown review type: " + type);
            }
            review.setRating(rating);
            review.setComment(comment);
            reviewService.create(review);
        }
        return "redirect:/customer/" + id + "/reviews";
    }

    @GetMapping("/{id}/profile")
    public String profile(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.getById(id));
        return "customer/profile";
    }

    @PostMapping("/{id}/profile")
    public String profileSubmit(@PathVariable Long id, @RequestParam String name, @RequestParam String email,
            @RequestParam(required = false) String dietaryPreferences) {
        Customer updates = new Customer();
        updates.setName(name);
        updates.setEmail(email);
        if (dietaryPreferences != null && !dietaryPreferences.isBlank()) {
            updates.setDietaryPreferences(new ArrayList<>(Arrays.asList(dietaryPreferences.split("\\s*,\\s*"))));
        }
        customerService.update(id, updates);
        return "redirect:/customer/" + id + "/profile";
    }

    @GetMapping("/{id}/providers")
    public String providers(@PathVariable Long id, @RequestParam(required = false) String query, Model model) {
        customerService.getById(id);
        List<Provider> providers = (query == null || query.isBlank())
                ? providerService.getAll()
                : providerService.findBySpecialty(query.trim());
        model.addAttribute("customerId", id);
        model.addAttribute("providers", providers);
        model.addAttribute("query", query != null ? query : "");
        return "customer/providers";
    }

    @GetMapping("/{id}/providers/{providerId}")
    public String providerProfile(@PathVariable Long id, @PathVariable Long providerId, Model model) {
        customerService.getById(id);
        Provider provider = providerService.getById(providerId);
        model.addAttribute("customerId", id);
        model.addAttribute("provider", provider);
        model.addAttribute("stats", providerService.getProviderStats(providerId));
        return "customer/provider-profile";
    }

    @GetMapping("/{id}/providers/{providerId}/uploads")
    public String providerUploads(@PathVariable Long id, @PathVariable Long providerId, Model model) {
        customerService.getById(id);
        Provider provider = providerService.getById(providerId);
        ActionSets sets = actionSets(id);
        List<ContentCard> uploads = new ArrayList<>();
        recipeService.getByProviderId(providerId).forEach(recipe -> uploads.add(recipeCard(recipe, sets)));
        mealplanService.getByProviderId(providerId).forEach(mealplan -> uploads.add(mealplanCard(mealplan, sets)));
        mealkitService.getByProviderId(providerId).forEach(mealkit -> uploads.add(mealkitCard(mealkit, sets)));
        model.addAttribute("customerId", id);
        model.addAttribute("provider", provider);
        model.addAttribute("uploads", uploads);
        return "customer/provider-uploads";
    }

    private record ActionSets(Set<Long> savedRecipeIds, Set<Long> savedMealplanIds, Set<Long> subscribedMealkitIds) {
    }

    private ActionSets actionSets(Long customerId) {
        List<Save> saves = saveService.getByCustomer(customerId);
        Set<Long> savedRecipeIds = saves.stream()
                .filter(save -> save.getRecipe() != null)
                .map(save -> save.getRecipe().getId())
                .collect(Collectors.toSet());
        Set<Long> savedMealplanIds = saves.stream()
                .filter(save -> save.getMealplan() != null)
                .map(save -> save.getMealplan().getId())
                .collect(Collectors.toSet());
        Set<Long> subscribedMealkitIds = subscriptionService.getByCustomer(customerId).stream()
                .map(subscription -> subscription.getMealkit().getId())
                .collect(Collectors.toSet());
        return new ActionSets(savedRecipeIds, savedMealplanIds, subscribedMealkitIds);
    }

    private ContentCard recipeCard(Recipe recipe, ActionSets sets) {
        List<Review> reviews = reviewService.getByRecipe(recipe.getId());
        return new ContentCard("recipe", recipe.getId(), recipe.getTitle(),
                providerName(recipe.getProvider()), providerId(recipe.getProvider()),
                recipe.getTags() != null ? recipe.getTags() : List.of(),
                recipe.getIngredients() != null ? recipe.getIngredients() : List.of(),
                List.of(),
                null, null, null, null,
                averageRating(reviews), reviews.size(),
                sets.savedRecipeIds().contains(recipe.getId()));
    }

    private ContentCard mealplanCard(Mealplan mealplan, ActionSets sets) {
        List<Review> reviews = reviewService.getByMealplan(mealplan.getId());
        return new ContentCard("mealplan", mealplan.getId(), mealplan.getTitle(),
                providerName(mealplan.getProvider()), providerId(mealplan.getProvider()),
                mealplan.getCategory() != null ? List.of(mealplan.getCategory()) : List.of(),
                List.of(),
                recipeTitles(mealplan.getRecipes()),
                mealplan.getDescription(), mealplan.getSchedule(), mealplan.getDuration(), null,
                averageRating(reviews), reviews.size(),
                sets.savedMealplanIds().contains(mealplan.getId()));
    }

    private ContentCard mealkitCard(Mealkit mealkit, ActionSets sets) {
        List<Review> reviews = reviewService.getByMealkit(mealkit.getId());
        return new ContentCard("mealkit", mealkit.getId(), mealkit.getTitle(),
                providerName(mealkit.getProvider()), providerId(mealkit.getProvider()),
                mealkit.getCategory() != null ? List.of(mealkit.getCategory()) : List.of(),
                mealkit.getIngredients() != null ? mealkit.getIngredients() : List.of(),
                recipeTitles(mealkit.getRecipes()),
                mealkit.getDescription(), null, mealkit.getDuration(), mealkit.getPrice(),
                averageRating(reviews), reviews.size(),
                sets.subscribedMealkitIds().contains(mealkit.getId()));
    }

    private ReviewFormCard formCard(ContentCard card, Map<String, Review> existingByKey) {
        Review existing = existingByKey.get(card.type() + ":" + card.id());
        return new ReviewFormCard(card,
                existing != null ? existing.getRating() : null,
                existing != null ? existing.getComment() : null);
    }

    private String reviewKey(Review review) {
        if (review.getRecipe() != null) {
            return "recipe:" + review.getRecipe().getId();
        }
        if (review.getMealplan() != null) {
            return "mealplan:" + review.getMealplan().getId();
        }
        if (review.getMealkit() != null) {
            return "mealkit:" + review.getMealkit().getId();
        }
        return "none";
    }

    private List<String> recipeTitles(List<Recipe> recipes) {
        if (recipes == null) {
            return List.of();
        }
        return recipes.stream().map(Recipe::getTitle).toList();
    }

    private String providerName(Provider provider) {
        return provider != null ? provider.getName() : "Unknown Provider";
    }

    private Long providerId(Provider provider) {
        return provider != null ? provider.getId() : null;
    }

    private double averageRating(List<Review> reviews) {
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    // Only allows redirect targets within this customer's own pages.
    private String redirectTo(String redirect, Long customerId, String fallbackPage) {
        String base = "/customer/" + customerId + "/";
        if (redirect != null && redirect.startsWith(base)) {
            return "redirect:" + redirect;
        }
        return "redirect:" + base + fallbackPage;
    }
}
