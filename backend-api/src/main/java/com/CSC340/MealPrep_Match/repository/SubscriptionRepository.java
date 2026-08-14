package com.CSC340.MealPrep_Match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CSC340.MealPrep_Match.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByCustomer_Id(Long customerId);

    List<Subscription> findByMealkit_Provider_Id(Long providerId);

    Optional<Subscription> findByCustomer_IdAndMealkit_Id(Long customerId, Long mealkitId);

    boolean existsByCustomer_IdAndMealkit_Id(Long customerId, Long mealkitId);
}
