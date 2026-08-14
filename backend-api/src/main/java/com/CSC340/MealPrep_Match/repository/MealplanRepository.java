package com.CSC340.MealPrep_Match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CSC340.MealPrep_Match.entity.Mealplan;

public interface MealplanRepository extends JpaRepository<Mealplan, Long>{
    List<Mealplan> findByProviderId(Long providerId);

    /** Every mealplan whose recipe list contains the given recipe (used when deleting a recipe). */
    List<Mealplan> findByRecipes_Id(Long recipeId);

    List<Mealplan> findByCategoryContainingIgnoreCase(String category);

    List<Mealplan> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleQuery, String descriptionQuery);
}
