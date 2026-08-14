package com.CSC340.MealPrep_Match.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.repository.MealplanRepository;

@Service
public class MealplanService {

    private final MealplanRepository mealplanRepository;

    public MealplanService(MealplanRepository mealplanRepository) {
        this.mealplanRepository = mealplanRepository;
    }

    public List<Mealplan> getAll() {
        return mealplanRepository.findAll();
    }

    public Mealplan getById(Long id) {
        return mealplanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mealplan not found: " + id));
    }

    public List<Mealplan> getByCategory(String category) {
        return mealplanRepository.findByCategoryContainingIgnoreCase(category);
    }

    public List<Mealplan> getByProviderId(Long providerId) {
        return mealplanRepository.findByProviderId(providerId);
    }

    public Mealplan create(Mealplan mealplan) {
        return mealplanRepository.save(mealplan);
    }

    public Mealplan update(Long id, Mealplan updates) {
        Mealplan mealplan = getById(id);
        if (updates.getTitle() != null) {
            mealplan.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            mealplan.setDescription(updates.getDescription());
        }
        if (updates.getDuration() != null) {
            mealplan.setDuration(updates.getDuration());
        }
        if (updates.getSchedule() != null) {
            mealplan.setSchedule(updates.getSchedule());
        }
        if (updates.getCategory() != null) {
            mealplan.setCategory(updates.getCategory());
        }
        if (updates.getRecipes() != null) {
            mealplan.setRecipes(updates.getRecipes());
        }
        return mealplanRepository.save(mealplan);
    }

    public List<Mealplan> searchByTitle(String query) {
        return mealplanRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

}
