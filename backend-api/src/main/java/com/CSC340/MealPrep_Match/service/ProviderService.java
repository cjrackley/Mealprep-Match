package com.CSC340.MealPrep_Match.service;

import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CSC340.MealPrep_Match.repository.ProviderRepository;
import com.CSC340.MealPrep_Match.repository.ReviewRepository;
import com.CSC340.MealPrep_Match.repository.SaveRepository;
import com.CSC340.MealPrep_Match.repository.SubscriptionRepository;

import jakarta.transaction.Transactional;

import com.CSC340.MealPrep_Match.dto.ProviderStats;
import com.CSC340.MealPrep_Match.dto.UploadSummary;
import com.CSC340.MealPrep_Match.entity.Provider;
import com.CSC340.MealPrep_Match.entity.Review;
import com.CSC340.MealPrep_Match.entity.Subscription;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SaveRepository saveRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ProviderService(ProviderRepository providerRepository, SubscriptionRepository subscriptionRepository,
            SaveRepository saveRepository, ReviewRepository reviewRepository) {
        this.providerRepository = providerRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.saveRepository = saveRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Provider findById(long id) {
        return providerRepository.findById(id).orElse(null);
    }

    public Provider createProvider(Provider provider) {
        provider.setPasswordHash(passwordEncoder.encode(provider.getPassword()));
        provider.setPassword(null);
        return providerRepository.save(provider);
    }

    public boolean checkPassword(Provider provider, String rawPassword) {
        return passwordEncoder.matches(rawPassword, provider.getPasswordHash());
    }

    public Provider updateProviderInfo(Long id, Provider updatedProvider) {
        Optional<Provider> existingProvider = providerRepository.findById(id);
        if (existingProvider.isPresent()) {
            Provider provider = existingProvider.get();
            if (updatedProvider.getName() != null) {
                provider.setName(updatedProvider.getName());
            }
            if (updatedProvider.getEmail() != null) {
                provider.setEmail(updatedProvider.getEmail());
            }
            if (updatedProvider.getPassword() != null) {
                provider.setPasswordHash(passwordEncoder.encode(updatedProvider.getPassword()));
            }
            if (updatedProvider.getBio() != null) {
                provider.setBio(updatedProvider.getBio());
            }
            if (updatedProvider.getSpecialties() != null) {
                provider.setSpecialties(updatedProvider.getSpecialties());
            }
            if (updatedProvider.getVerified() != null) {
                provider.setVerified(updatedProvider.getVerified());
            }
            return providerRepository.save(provider);
        } else {
            throw new RuntimeException("Provider not found with id: " + id);
        }
    }

    @Transactional
    public ProviderStats getProviderStats(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + providerId));

        long contentCount = provider.getRecipes().size()
                + provider.getMealplans().size()
                + provider.getMealkits().size();

        List<Subscription> subscriptions = subscriptionRepository.findByMealkit_Provider_Id(providerId);
        long subscriptionCount = subscriptions.size();

        double totalRevenue = subscriptions.stream()
                .map(s -> s.getMealkit().getPrice())
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        long saveCount = saveRepository.findByRecipe_Provider_Id(providerId).size()
                + saveRepository.findByMealplan_Provider_Id(providerId).size();

        List<Review> reviews = getProviderReviews(providerId);
        long reviewCount = reviews.size();

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return new ProviderStats(averageRating, reviewCount, subscriptionCount, contentCount, saveCount,
                totalRevenue);
    }

    @Transactional
    public List<UploadSummary> getRecentUploads(Long providerId, int limit) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + providerId));

        List<UploadSummary> uploads = new ArrayList<>();
        provider.getRecipes().forEach(r -> uploads.add(new UploadSummary("Recipe", r.getTitle(), r.getId(),
                r.getTags() != null ? r.getTags() : List.of())));
        provider.getMealplans().forEach(m -> uploads.add(new UploadSummary("Mealplan", m.getTitle(), m.getId(),
                m.getCategory() != null ? List.of(m.getCategory()) : List.of())));
        provider.getMealkits().forEach(k -> uploads.add(new UploadSummary("Mealkit", k.getTitle(), k.getId(),
                k.getCategory() != null ? List.of(k.getCategory()) : List.of())));

        return uploads.stream()
                .sorted(Comparator.comparing(UploadSummary::getId).reversed())
                .limit(limit)
                .toList();
    }

    @Transactional
    public List<UploadSummary> getAllUploads(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + providerId));

        List<UploadSummary> uploads = new ArrayList<>();

        provider.getRecipes().forEach(r -> uploads.add(new UploadSummary("Recipe", r.getTitle(), r.getId(),
                r.getTags() != null ? r.getTags() : List.of())));
        provider.getMealplans().forEach(m -> uploads.add(new UploadSummary("Mealplan", m.getTitle(), m.getId(),
                m.getCategory() != null ? List.of(m.getCategory()) : List.of())));
        provider.getMealkits().forEach(k -> uploads.add(new UploadSummary("Mealkit", k.getTitle(), k.getId(),
                k.getCategory() != null ? List.of(k.getCategory()) : List.of())));

        return uploads.stream()
                .sorted(Comparator.comparing(UploadSummary::getId).reversed())
                .toList();

    }

    @Transactional
    public List<Review> getRecentReviews(Long providerId, int limit) {
        return getProviderReviews(providerId).stream()
                .sorted(Comparator.comparing(Review::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }

    private List<Review> getProviderReviews(Long providerId) {
        List<Review> reviews = new ArrayList<>();
        reviews.addAll(reviewRepository.findByRecipe_Provider_Id(providerId));
        reviews.addAll(reviewRepository.findByMealplan_Provider_Id(providerId));
        reviews.addAll(reviewRepository.findByMealkit_Provider_Id(providerId));
        return reviews;
    }

    public void deleteProvider(long id) {
        providerRepository.deleteById(id);
    }

    public Provider findByEmail(String email) {
        return providerRepository.findByEmail(email);
    }

    public List<Provider> findBySpecialty(String specialty) {
        return providerRepository.findBySpecialty(specialty);
    }

    public InputStream getProviderImageStreamInsideTx(Long providerId) {
        Provider provider = providerRepository.findById(providerId).orElse(null);
        try {
            if (provider != null && provider.getProfilePicture() != null) {
                return provider.getProfilePicture().getBinaryStream();
            } else {
                ClassPathResource defaultImage = new ClassPathResource("static/images/provider-default.jpg");
                return defaultImage.getInputStream();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving picture for provider with id: " + providerId, e);
        }
    }

    @Transactional
    public void saveProviderProfilePicture(Long providerId, InputStream profilePictureStream) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found with id:" + providerId));
        try {
            Blob profilePictureBlob = new javax.sql.rowset.serial.SerialBlob(profilePictureStream.readAllBytes());
            provider.setProfilePicture(profilePictureBlob);
            providerRepository.save(provider);
        } catch (Exception e) {
            throw new RuntimeException("Error saving profile picture for provider with id: " + providerId, e);
        }
    }

}
