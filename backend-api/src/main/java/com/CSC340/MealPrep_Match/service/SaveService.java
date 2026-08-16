package com.CSC340.MealPrep_Match.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Customer;
import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.entity.Save;
import com.CSC340.MealPrep_Match.repository.CustomerRepository;
import com.CSC340.MealPrep_Match.repository.MealplanRepository;
import com.CSC340.MealPrep_Match.repository.RecipeRepository;
import com.CSC340.MealPrep_Match.repository.SaveRepository;

@Service
public class SaveService {

    private final SaveRepository saveRepository;
    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;
    private final MealplanRepository mealplanRepository;

    public SaveService(SaveRepository saveRepository, CustomerRepository customerRepository,
            RecipeRepository recipeRepository, MealplanRepository mealplanRepository) {
        this.saveRepository = saveRepository;
        this.customerRepository = customerRepository;
        this.recipeRepository = recipeRepository;
        this.mealplanRepository = mealplanRepository;
    }

    public List<Save> getByCustomer(Long customerId) {
        return saveRepository.findByCustomer_Id(customerId);
    }

    public Save getById(Long id) {
        return saveRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Save not found: " + id));
    }

    public Save create(Save save) {
        if (save.getCustomer() == null || save.getCustomer().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customer.id is required");
        }

        boolean hasRecipe = save.getRecipe() != null && save.getRecipe().getId() != null;
        boolean hasMealplan = save.getMealplan() != null && save.getMealplan().getId() != null;
        if (hasRecipe == hasMealplan) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide exactly one of recipe.id or mealplan.id");
        }

        Customer customer = customerRepository.findById(save.getCustomer().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found: " + save.getCustomer().getId()));

        if (hasRecipe) {
            Recipe recipe = recipeRepository.findById(save.getRecipe().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Recipe not found: " + save.getRecipe().getId()));

            return saveRepository.findByCustomer_IdAndRecipe_Id(customer.getId(), recipe.getId())
                    .orElseGet(() -> {
                        save.setCustomer(customer);
                        save.setRecipe(recipe);
                        save.setMealplan(null);
                        save.setSavedAt(Instant.now());
                        return saveRepository.save(save);
                    });
        } else {
            Mealplan mealplan = mealplanRepository.findById(save.getMealplan().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Mealplan not found: " + save.getMealplan().getId()));

            return saveRepository.findByCustomer_IdAndMealplan_Id(customer.getId(), mealplan.getId())
                    .orElseGet(() -> {
                        save.setCustomer(customer);
                        save.setMealplan(mealplan);
                        save.setRecipe(null);
                        save.setSavedAt(Instant.now());
                        return saveRepository.save(save);
                    });
        }
    }

    public void delete(Long id) {
        if (!saveRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Save not found: " + id);
        }
        saveRepository.deleteById(id);
    }

    public void deleteByCustomerAndRecipe(Long customerId, Long recipeId) {
        Save save = saveRepository.findByCustomer_IdAndRecipe_Id(customerId, recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Save not found for customer " + customerId + " and recipe " + recipeId));
        saveRepository.delete(save);
    }

    public void deleteByCustomerAndMealplan(Long customerId, Long mealplanId) {
        Save save = saveRepository.findByCustomer_IdAndMealplan_Id(customerId, mealplanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Save not found for customer " + customerId + " and mealplan " + mealplanId));
        saveRepository.delete(save);
    }
}
