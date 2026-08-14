package com.CSC340.MealPrep_Match.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.repository.MealkitRepository;

@Service
public class MealkitService {
    private final MealkitRepository mealkitRepository;

    public MealkitService(MealkitRepository mealkitRepository) {
        this.mealkitRepository = mealkitRepository;

    }
        public List<Mealkit> getAllMealkits() {
            return mealkitRepository.findAll();
        }

        public Mealkit getMealkitById(Long id) {
            return mealkitRepository.findById(id).orElse(null);
        }

        public List<Mealkit> getMealkitsByCategory(String category) {
            return mealkitRepository.findByCategoryContainingIgnoreCase(category);
        }

        public Mealkit createMealkit(Mealkit mealkit) {
            return mealkitRepository.save(mealkit);
        }

        public List<Mealkit> getMealkitByProviderId(Long providerId) {
            return mealkitRepository.findByProviderId(providerId);
        }

        public Mealkit updateMealkit(Long id, Mealkit updatedMealkit) {
            return mealkitRepository.findById(id)
                .map(mealkit -> {
                    mealkit.setTitle(updatedMealkit.getTitle());
                    mealkit.setDuration(updatedMealkit.getDuration());
                    mealkit.setDescription(updatedMealkit.getDescription());
                    mealkit.setCategory(updatedMealkit.getCategory());
                    mealkit.setIngredients(updatedMealkit.getIngredients());
                    if (updatedMealkit.getPrice() != null) {
                        mealkit.setPrice(updatedMealkit.getPrice());
                    }
                    return mealkitRepository.save(mealkit);
                })
                .orElseThrow(() -> new RuntimeException("Mealkit not found with id: " + id));
        }

        public void deleteMealkit(Long id) {
            mealkitRepository.deleteById(id);
        }

        public List<Mealkit> searchMealkitsByTitle(String query) {
            return mealkitRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        }
}

