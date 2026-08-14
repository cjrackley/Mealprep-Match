package com.CSC340.MealPrep_Match.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.repository.MealkitRepository;

@Service
public class MealkitService {

    private final MealkitRepository mealkitRepository;

    public MealkitService(MealkitRepository mealkitRepository) {
        this.mealkitRepository = mealkitRepository;
    }

    public List<Mealkit> getAll() {
        return mealkitRepository.findAll();
    }

    public Mealkit getById(Long id) {
        return mealkitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mealkit not found: " + id));
    }

    public List<Mealkit> getByCategory(String category) {
        return mealkitRepository.findByCategoryContainingIgnoreCase(category);
    }

    public List<Mealkit> getByProviderId(Long providerId) {
        return mealkitRepository.findByProviderId(providerId);
    }

    public Mealkit create(Mealkit mealkit) {
        return mealkitRepository.save(mealkit);
    }

    public Mealkit update(Long id, Mealkit updates) {
        Mealkit mealkit = getById(id);
        if (updates.getTitle() != null) {
            mealkit.setTitle(updates.getTitle());
        }
        if (updates.getDuration() != null) {
            mealkit.setDuration(updates.getDuration());
        }
        if (updates.getDescription() != null) {
            mealkit.setDescription(updates.getDescription());
        }
        if (updates.getCategory() != null) {
            mealkit.setCategory(updates.getCategory());
        }
        if (updates.getIngredients() != null) {
            mealkit.setIngredients(updates.getIngredients());
        }
        if (updates.getPrice() != null) {
            mealkit.setPrice(updates.getPrice());
        }
        if (updates.getRecipes() != null) {
            mealkit.setRecipes(updates.getRecipes());
        }
        return mealkitRepository.save(mealkit);
    }

    public List<Mealkit> searchByTitle(String query) {
        return mealkitRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
}
