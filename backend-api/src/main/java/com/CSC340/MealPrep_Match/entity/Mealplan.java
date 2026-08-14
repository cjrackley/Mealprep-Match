package com.CSC340.MealPrep_Match.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mealplans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mealplan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({ "mealplans" })
    @JoinColumn(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String schedule;

    private String category;

    @ManyToMany
    @JoinTable(
            name = "mealplan_recipes",
            joinColumns = @JoinColumn(name = "mealplan_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    @JsonIgnore
    private List<Recipe> recipes;
}
