package com.CSC340.MealPrep_Match.dto;

import java.util.List;

/**
 * View model for a single piece of provider content (recipe, mealplan, or mealkit)
 * rendered as a card in the customer UI. Fields that do not apply to a given type
 * are null or empty ("actioned" means saved for recipes/mealplans, subscribed for mealkits).
 */
public record ContentCard(
        String type,
        Long id,
        String title,
        String providerName,
        Long providerId,
        List<String> tags,
        List<String> ingredients,
        String description,
        String schedule,
        String duration,
        Double price,
        double averageRating,
        int reviewCount,
        boolean actioned) {
}
