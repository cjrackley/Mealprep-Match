package com.CSC340.MealPrep_Match.dto;

public record ReviewFormCard(
        ContentCard content,
        Integer existingRating,
        String existingComment) {
}
