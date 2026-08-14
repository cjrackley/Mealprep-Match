package com.CSC340.MealPrep_Match.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CSC340.MealPrep_Match.entity.Customer;
import com.CSC340.MealPrep_Match.entity.Mealkit;
import com.CSC340.MealPrep_Match.entity.Subscription;
import com.CSC340.MealPrep_Match.repository.CustomerRepository;
import com.CSC340.MealPrep_Match.repository.MealkitRepository;
import com.CSC340.MealPrep_Match.repository.SubscriptionRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final MealkitRepository mealkitRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, CustomerRepository customerRepository,
            MealkitRepository mealkitRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
        this.mealkitRepository = mealkitRepository;
    }

    public List<Subscription> getAll() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> getByCustomer(Long customerId) {
        return subscriptionRepository.findByCustomer_Id(customerId);
    }

    public Subscription getById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found: " + id));
    }

    public Subscription create(Subscription subscription) {
        if (subscription.getCustomer() == null || subscription.getCustomer().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customer.id is required");
        }
        if (subscription.getMealkit() == null || subscription.getMealkit().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mealkit.id is required");
        }

        Customer customer = customerRepository.findById(subscription.getCustomer().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found: " + subscription.getCustomer().getId()));
        Mealkit mealkit = mealkitRepository.findById(subscription.getMealkit().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Mealkit not found: " + subscription.getMealkit().getId()));

        return subscriptionRepository.findByCustomer_IdAndMealkit_Id(customer.getId(), mealkit.getId())
                .orElseGet(() -> {
                    subscription.setCustomer(customer);
                    subscription.setMealkit(mealkit);
                    subscription.setSubscribedAt(Instant.now());
                    return subscriptionRepository.save(subscription);
                });
    }

    public void delete(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    public void deleteByCustomerAndMealkit(Long customerId, Long mealkitId) {
        Subscription subscription = subscriptionRepository
                .findByCustomer_IdAndMealkit_Id(customerId, mealkitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Subscription not found for customer " + customerId + " and mealkit " + mealkitId));
        subscriptionRepository.delete(subscription);
    }
}
