package com.CSC340.MealPrep_Match.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.CSC340.MealPrep_Match.entity.Provider;
import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.service.ContentDeletionService;
import com.CSC340.MealPrep_Match.service.MealkitService;
import com.CSC340.MealPrep_Match.service.MealplanService;
import com.CSC340.MealPrep_Match.service.ProviderService;
import com.CSC340.MealPrep_Match.service.RecipeService;
import com.CSC340.MealPrep_Match.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/provider")
public class ProviderUiController {
    private final ProviderService providerService;
    private final RecipeService recipeService;
    private final MealplanService mealplanService;
    private final MealkitService mealkitService;
    private final ReviewService reviewService;
    private final ContentDeletionService contentDeletionService;

    private final TransactionTemplate transactionTemplate;

    public ProviderUiController(ProviderService providerService, RecipeService recipeService,
            MealplanService mealplanService, MealkitService mealkitService, ReviewService reviewService,
            ContentDeletionService contentDeletionService, TransactionTemplate transactionTemplate) {
        this.providerService = providerService;
        this.recipeService = recipeService;
        this.mealplanService = mealplanService;
        this.mealkitService = mealkitService;
        this.reviewService = reviewService;
        this.contentDeletionService = contentDeletionService;
        this.transactionTemplate = transactionTemplate;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("provider", new Provider());
        return "provider/provider-registration";
    }

    @PostMapping("/signup")
    public String registerProvider(Provider provider, MultipartFile profilePictureFile, HttpSession session) {
        Provider created = providerService.create(provider);
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            try {
                providerService.saveProviderProfilePicture(created.getId(), profilePictureFile.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        session.setAttribute("providerId", created.getId());
        return "redirect:/provider/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "provider/provider-login";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @RequestParam String email, @RequestParam String password) {
        Provider provider = providerService.findByEmail(email);
        if (provider != null && providerService.checkPassword(provider, password)) {
            session.setAttribute("providerId", provider.getId());
            return "redirect:/provider/dashboard";
        }
        return "redirect:/provider/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Provider provider = providerService.getById(providerId);
        model.addAttribute("provider", provider);
        model.addAttribute("stats", providerService.getProviderStats(providerId));
        model.addAttribute("recentUploads", providerService.getRecentUploads(providerId, 5));
        model.addAttribute("recentReviews", providerService.getRecentReviews(providerId, 5));

        return "provider/provider-dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Provider provider = providerService.getById(providerId);
        model.addAttribute("provider", provider);

        return "/provider/provider-profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileInformation(HttpSession session, Model model) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Provider provider = providerService.getById(providerId);
        model.addAttribute("provider", provider);
        return "/provider/provider-profile-customization";
    }

    @PostMapping("/profile/edit")
    public String updateProfileInformation(Provider provider, MultipartFile profilePictureFile, HttpSession session) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            try {
                providerService.saveProviderProfilePicture(providerId, profilePictureFile.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        providerService.update(providerId, provider);
        return "redirect:/provider/profile";
    }

    @GetMapping("/picture/{providerId}")
    public ResponseEntity<StreamingResponseBody> streamProviderImage(@PathVariable Long providerId) {

        StreamingResponseBody stream = outputStream -> {
            transactionTemplate.execute(status -> {
                try (InputStream imageStream = providerService.getProviderImageStreamInsideTx(providerId)) {
                    StreamUtils.copy(imageStream, outputStream);
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Streaming failed", e);
                }
                return null;
            });
        };

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(stream);
    }

    @GetMapping("/uploads")
    public String uploads(HttpSession session, Model model) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Provider provider = providerService.getById(providerId);
        model.addAttribute("provider", provider);
        model.addAttribute("uploads", providerService.getAllUploads(providerId));

        return "provider/provider-uploads";
    }

    @GetMapping("/uploads/create")
    public String createUpload(HttpSession session, Model model) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }
        // Mealplans and mealkits are assembled from recipes this provider already published.
        model.addAttribute("recipes", recipeService.getByProviderId(providerId));
        return "provider/provider-upload";
    }

    @PostMapping("/uploads/create/recipe")
    public String createRecipe(HttpSession session, @RequestParam String title,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String instructions,
            @RequestParam(required = false) String tags) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Recipe recipe = new Recipe();
        recipe.setProvider(providerService.getById(providerId));
        recipe.setTitle(title);
        recipe.setInstructions(instructions);
        recipe.setIngredients(splitCsv(ingredients));
        recipe.setTags(splitCsv(tags));
        recipeService.create(recipe);

        return "redirect:/provider/uploads";
    }

    @PostMapping("/uploads/create/mealplan")
    public String createMealplan(HttpSession session, @RequestParam String title,
            @RequestParam String duration,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String schedule,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<Long> recipeIds) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Mealplan mealplan = new Mealplan();
        mealplan.setProvider(providerService.getById(providerId));
        mealplan.setTitle(title);
        mealplan.setDuration(duration);
        mealplan.setDescription(description);
        mealplan.setSchedule(schedule);
        mealplan.setCategory(category);
        mealplan.setRecipes(ownRecipes(recipeIds, providerId));
        mealplanService.create(mealplan);

        return "redirect:/provider/uploads";
    }

    @PostMapping("/uploads/create/mealkit")
    public String createMealkit(HttpSession session, @RequestParam String title,
            @RequestParam String duration,
            @RequestParam String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) List<Long> recipeIds) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        Mealkit mealkit = new Mealkit();
        mealkit.setProvider(providerService.getById(providerId));
        mealkit.setTitle(title);
        mealkit.setDuration(duration);
        mealkit.setDescription(description);
        mealkit.setCategory(category);
        mealkit.setPrice(price);
        // A kit's ingredient list is curated by the provider, not derived from its recipes.
        mealkit.setIngredients(splitCsv(ingredients));
        mealkit.setRecipes(ownRecipes(recipeIds, providerId));
        mealkitService.create(mealkit);

        return "redirect:/provider/uploads";
    }

    @PostMapping("/uploads/recipe/{recipeId}/delete")
    public String deleteRecipe(HttpSession session, @PathVariable Long recipeId) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }
        contentDeletionService.deleteRecipe(recipeId, providerId);
        return "redirect:/provider/uploads";
    }

    @PostMapping("/uploads/mealplan/{mealplanId}/delete")
    public String deleteMealplan(HttpSession session, @PathVariable Long mealplanId) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }
        contentDeletionService.deleteMealplan(mealplanId, providerId);
        return "redirect:/provider/uploads";
    }

    @PostMapping("/uploads/mealkit/{mealkitId}/delete")
    public String deleteMealkit(HttpSession session, @PathVariable Long mealkitId) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }
        contentDeletionService.deleteMealkit(mealkitId, providerId);
        return "redirect:/provider/uploads";
    }

    @PostMapping("/reviews/{reviewId}/reply")
    public String replyToReview(HttpSession session, @PathVariable Long reviewId,
            @RequestParam String reply) {
        Long providerId = (Long) session.getAttribute("providerId");
        if (providerId == null) {
            return "redirect:/provider/login";
        }
        reviewService.reply(reviewId, providerId, reply);
        return "redirect:/provider/dashboard";
    }

    /**
     * Resolves the recipe ids posted by a picker, keeping only recipes this provider owns
     * so a crafted form cannot pull another provider's recipe into a plan or kit.
     */
    private List<Recipe> ownRecipes(List<Long> recipeIds, Long providerId) {
        if (recipeIds == null || recipeIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> requested = new HashSet<>(recipeIds);
        return recipeService.getByProviderId(providerId).stream()
                .filter(recipe -> requested.contains(recipe.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> splitCsv(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

}
