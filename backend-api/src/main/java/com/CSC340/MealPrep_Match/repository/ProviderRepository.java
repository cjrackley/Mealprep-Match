package com.CSC340.MealPrep_Match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.CSC340.MealPrep_Match.entity.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Provider findByEmail(String email);

    @Query("SELECT DISTINCT p FROM Provider p JOIN p.specialties s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :specialty, '%'))")
    List<Provider> findBySpecialty(@Param("specialty") String specialty);
}
