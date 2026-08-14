package com.CSC340.MealPrep_Match.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Customer;
import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.entity.Mealplan;
import com.CSC340.MealPrep_Match.entity.Provider;
import com.CSC340.MealPrep_Match.entity.Recipe;
import com.CSC340.MealPrep_Match.entity.Review;
import com.CSC340.MealPrep_Match.repository.CustomerRepository;
import com.CSC340.MealPrep_Match.repository.MealkitRepository;
import com.CSC340.MealPrep_Match.repository.MealplanRepository;
import com.CSC340.MealPrep_Match.repository.RecipeRepository;
import com.CSC340.MealPrep_Match.repository.ReviewRepository;
import com.CSC340.MealPrep_Match.repository.SubscriptionRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;
    private final MealplanRepository mealplanRepository;
    private final MealkitRepository mealkitRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ReviewService(ReviewRepository reviewRepository, CustomerRepository customerRepository,
            RecipeRepository recipeRepository, MealplanRepository mealplanRepository,
            MealkitRepository mealkitRepository, SubscriptionRepository subscriptionRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.recipeRepository = recipeRepository;
        this.mealplanRepository = mealplanRepository;
        this.mealkitRepository = mealkitRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public List<Review> getByRecipe(Long recipeId) {
        return reviewRepository.findByRecipe_Id(recipeId);
    }

    public List<Review> getByMealplan(Long mealplanId) {
        return reviewRepository.findByMealplan_Id(mealplanId);
    }

    public List<Review> getByMealkit(Long mealkitId) {
        return reviewRepository.findByMealkit_Id(mealkitId);
    }

    public List<Review> getByCustomer(Long customerId) {
        return reviewRepository.findByCustomer_Id(customerId);
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: " + id));
    }

    public Review create(Review review) {
        if (review.getCustomer() == null || review.getCustomer().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customer.id is required");
        }

        boolean hasRecipe = review.getRecipe() != null && review.getRecipe().getId() != null;
        boolean hasMealplan = review.getMealplan() != null && review.getMealplan().getId() != null;
        boolean hasMealkit = review.getMealkit() != null && review.getMealkit().getId() != null;
        int targetCount = (hasRecipe ? 1 : 0) + (hasMealplan ? 1 : 0) + (hasMealkit ? 1 : 0);
        if (targetCount != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide exactly one of recipe.id, mealplan.id, or mealkit.id");
        }

        Customer customer = customerRepository.findById(review.getCustomer().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found: " + review.getCustomer().getId()));

        Recipe recipe = null;
        Mealplan mealplan = null;
        Mealkit mealkit = null;
        if (hasRecipe) {
            recipe = recipeRepository.findById(review.getRecipe().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Recipe not found: " + review.getRecipe().getId()));
        } else if (hasMealplan) {
            mealplan = mealplanRepository.findById(review.getMealplan().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Mealplan not found: " + review.getMealplan().getId()));
        } else {
            mealkit = mealkitRepository.findById(review.getMealkit().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Mealkit not found: " + review.getMealkit().getId()));
            // Mealkits are paid content: only subscribers may review them.
            if (!subscriptionRepository.existsByCustomer_IdAndMealkit_Id(customer.getId(), mealkit.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Customer must be subscribed to this mealkit before reviewing it");
            }
        }

        review.setCustomer(customer);
        review.setRecipe(recipe);
        review.setMealplan(mealplan);
        review.setMealkit(mealkit);
        review.setCreatedAt(Instant.now());
        return reviewRepository.save(review);
    }

    public Review update(Long id, Review updates) {
        Review review = getById(id);
        if (updates.getRating() != null) {
            review.setRating(updates.getRating());
        }
        if (updates.getComment() != null) {
            review.setComment(updates.getComment());
        }
        return reviewRepository.save(review);
    }

    public Review reply(Long reviewId, Long providerId, String replyText) {
        Review review = getById(reviewId);

        Provider owner;
        if (review.getRecipe() != null) {
            owner = review.getRecipe().getProvider();
        } else if (review.getMealplan() != null) {
            owner = review.getMealplan().getProvider();
        } else {
            owner = review.getMealkit().getProvider();
        }
        if (owner == null || !owner.getId().equals(providerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the provider of this content can reply to its reviews");
        }

        review.setProviderReply(replyText);
        review.setRepliedAt(Instant.now());
        return reviewRepository.save(review);
    }

    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
