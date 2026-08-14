package com.CSC340.MealPrep_Match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CSC340.MealPrep_Match.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.tags t WHERE LOWER(t) = LOWER(:tag)")
    List<Recipe> findByTag(@Param("tag") String tag);

    List<Recipe> findByProvider_Id(Long providerId);
}
