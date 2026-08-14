package com.CSC340.MealPrep_Match.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.entity.Provider;
import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.repository.MealkitRepository;
import com.CSC340.MealPrep_Match.repository.MealplanRepository;
import com.CSC340.MealPrep_Match.repository.RecipeRepository;
import com.CSC340.MealPrep_Match.repository.ReviewRepository;
import com.CSC340.MealPrep_Match.repository.SaveRepository;
import com.CSC340.MealPrep_Match.repository.SubscriptionRepository;

import jakarta.transaction.Transactional;

/**
 * The single delete path for provider content.
 * <p>
 * Recipes, mealplans and mealkits are all referenced by other tables — join tables
 * ({@code mealplan_recipes}, {@code mealkit_recipes}) and customer activity
 * ({@code saves}, {@code reviews}, {@code subscriptions}). None of those associations
 * cascade, so a bare {@code deleteById} fails on a foreign key constraint. Each method
 * here clears the dependents first, then deletes the content itself.
 * <p>
 * Every method takes the acting provider's id and refuses to touch another provider's
 * content, so callers do not have to remember to check ownership.
 */
@Service
public class ContentDeletionService {

    private final RecipeRepository recipeRepository;
    private final MealplanRepository mealplanRepository;
    private final MealkitRepository mealkitRepository;
    private final SaveRepository saveRepository;
    private final ReviewRepository reviewRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ContentDeletionService(RecipeRepository recipeRepository, MealplanRepository mealplanRepository,
            MealkitRepository mealkitRepository, SaveRepository saveRepository, ReviewRepository reviewRepository,
            SubscriptionRepository subscriptionRepository) {
        this.recipeRepository = recipeRepository;
        this.mealplanRepository = mealplanRepository;
        this.mealkitRepository = mealkitRepository;
        this.saveRepository = saveRepository;
        this.reviewRepository = reviewRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Deletes a recipe along with its saves, reviews, and its membership in any
     * mealplan or mealkit. The plans and kits themselves survive, one recipe shorter.
     */
    @Transactional
    public void deleteRecipe(Long recipeId, Long providerId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found: " + recipeId));
        requireOwner(recipe.getProvider(), providerId, "recipe");

        mealplanRepository.findByRecipes_Id(recipeId).forEach(mealplan -> {
            mealplan.getRecipes().removeIf(r -> r.getId().equals(recipeId));
            mealplanRepository.save(mealplan);
        });
        mealkitRepository.findByRecipes_Id(recipeId).forEach(mealkit -> {
            mealkit.getRecipes().removeIf(r -> r.getId().equals(recipeId));
            mealkitRepository.save(mealkit);
        });

        saveRepository.deleteAll(saveRepository.findByRecipe_Id(recipeId));
        reviewRepository.deleteAll(reviewRepository.findByRecipe_Id(recipeId));

        recipeRepository.delete(recipe);
    }

    /**
     * Deletes a mealplan along with its saves and reviews. Its recipes are only
     * unlinked, never deleted — they belong to the provider independently.
     */
    @Transactional
    public void deleteMealplan(Long mealplanId, Long providerId) {
        Mealplan mealplan = mealplanRepository.findById(mealplanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Mealplan not found: " + mealplanId));
        requireOwner(mealplan.getProvider(), providerId, "mealplan");

        saveRepository.deleteAll(saveRepository.findByMealplan_Id(mealplanId));
        reviewRepository.deleteAll(reviewRepository.findByMealplan_Id(mealplanId));

        if (mealplan.getRecipes() != null) {
            mealplan.getRecipes().clear();
            mealplanRepository.save(mealplan);
        }
        mealplanRepository.delete(mealplan);
    }

    /**
     * Deletes a mealkit along with its subscriptions and reviews. Mealkits are the
     * paid content type, so this cancels live subscriptions — the caller is expected
     * to have warned the provider first.
     */
    @Transactional
    public void deleteMealkit(Long mealkitId, Long providerId) {
        Mealkit mealkit = mealkitRepository.findById(mealkitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Mealkit not found: " + mealkitId));
        requireOwner(mealkit.getProvider(), providerId, "mealkit");

        subscriptionRepository.deleteAll(subscriptionRepository.findByMealkit_Id(mealkitId));
        reviewRepository.deleteAll(reviewRepository.findByMealkit_Id(mealkitId));

        if (mealkit.getRecipes() != null) {
            mealkit.getRecipes().clear();
            mealkitRepository.save(mealkit);
        }
        mealkitRepository.delete(mealkit);
    }

    private void requireOwner(Provider owner, Long providerId, String type) {
        if (owner == null || !owner.getId().equals(providerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not own this " + type + ".");
        }
    }
}
