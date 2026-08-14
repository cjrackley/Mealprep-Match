package com.CSC340.MealPrep_Match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CSC340.MealPrep_Match.entity.Save;

public interface SaveRepository extends JpaRepository<Save, Long> {

    List<Save> findByCustomer_Id(Long customerId);

    Optional<Save> findByCustomer_IdAndRecipe_Id(Long customerId, Long recipeId);

    Optional<Save> findByCustomer_IdAndMealplan_Id(Long customerId, Long mealplanId);

    List<Save> findByRecipe_Provider_Id(Long providerId);

    List<Save> findByMealplan_Provider_Id(Long providerId);
}
