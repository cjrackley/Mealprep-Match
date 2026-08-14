package com.CSC340.MealPrep_Match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CSC340.MealPrep_Match.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCustomer_Id(Long customerId);

    List<Review> findByRecipe_Id(Long recipeId);

    List<Review> findByMealplan_Id(Long mealplanId);

    List<Review> findByMealkit_Id(Long mealkitId);

    List<Review> findByRecipe_Provider_Id(Long providerId);

    List<Review> findByMealplan_Provider_Id(Long providerId);

    List<Review> findByMealkit_Provider_Id(Long providerId);
}
