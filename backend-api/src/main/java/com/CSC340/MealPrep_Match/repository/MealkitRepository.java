package com.CSC340.MealPrep_Match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CSC340.MealPrep_Match.entity.Mealkit;

public interface MealkitRepository extends JpaRepository<Mealkit, Long>{
    List<Mealkit> findByProviderId(Long providerId);

    /** Every mealkit whose recipe list contains the given recipe (used when deleting a recipe). */
    List<Mealkit> findByRecipes_Id(Long recipeId);

    List<Mealkit> findByCategoryContainingIgnoreCase(String category);

    List<Mealkit> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleQuery, String descriptionQuery);
    
}
