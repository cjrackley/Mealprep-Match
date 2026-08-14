package com.CSC340.MealPrep_Match.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.repository.MealplanRepository;

@Service
public class MealplanService {
    
    private final MealplanRepository mealplanRepository;

    public MealplanService(MealplanRepository mealplanRepository) {
        this.mealplanRepository = mealplanRepository;
    }

    public List<Mealplan> getAllMealplans() {
        return mealplanRepository.findAll();
    }

    public Mealplan getMealplanById(Long id) {
        return mealplanRepository.findById(id).orElse(null);
    }

    public List<Mealplan> getMealplansByCategory(String category) {
        return mealplanRepository.findByCategoryContainingIgnoreCase(category);
    }

    public Mealplan createMealplan(Mealplan mealplan) {
        return mealplanRepository.save(mealplan);
    }

    public List<Mealplan> getMealplansByProviderId(Long providerId) {
        return mealplanRepository.findByProviderId(providerId);
    }

    public Mealplan updateMealplan(Long id, Mealplan updatedMealplan) {
        return mealplanRepository.findById(id)
            .map(mealplan -> {
                mealplan.setTitle(updatedMealplan.getTitle());
                mealplan.setDescription(updatedMealplan.getDescription());
                mealplan.setDuration(updatedMealplan.getDuration());
                mealplan.setSchedule(updatedMealplan.getSchedule());
                mealplan.setCategory(updatedMealplan.getCategory());
                return mealplanRepository.save(mealplan);
            })
            .orElseThrow(() -> new RuntimeException("Mealplan not found with id: " + id));
    }

    public void deleteMealplan(Long id) {
        mealplanRepository.deleteById(id);
    }

    public List<Mealplan> searchMealplansByTitle(String query) {
        return mealplanRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

}
