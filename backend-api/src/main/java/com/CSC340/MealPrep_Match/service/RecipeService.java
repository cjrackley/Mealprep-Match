package com.CSC340.MealPrep_Match.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.repository.RecipeRepository;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    public List<Recipe> getByTag(String tag) {
        return recipeRepository.findByTag(tag);
    }

    public List<Recipe> getByProviderId(Long providerId) {
        return recipeRepository.findByProvider_Id(providerId);
    }

    public Recipe getById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found: " + id));
    }

    public Recipe create(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Recipe update(Long id, Recipe updates) {
        Recipe recipe = getById(id);
        if (updates.getTitle() != null) {
            recipe.setTitle(updates.getTitle());
        }
        if (updates.getIngredients() != null) {
            recipe.setIngredients(updates.getIngredients());
        }
        if (updates.getInstructions() != null) {
            recipe.setInstructions(updates.getInstructions());
        }
        if (updates.getTags() != null) {
            recipe.setTags(updates.getTags());
        }
        return recipeRepository.save(recipe);
    }

    public void delete(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found: " + id);
        }
        recipeRepository.deleteById(id);
    }
}
