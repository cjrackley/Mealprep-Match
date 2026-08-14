package com.CSC340.MealPrep_Match.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "mealkits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mealkit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({ "mealkits" })
    @JoinColumn(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private String description;

    private String category;

    private Double price;

    @ElementCollection
    @CollectionTable(name = "mealkit_ingredients", joinColumns = @JoinColumn(name = "mealkit_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    @ManyToMany
    @JoinTable(
            name = "mealkit_recipes",
            joinColumns = @JoinColumn(name = "mealkit_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    @JsonIgnore
    private List<Recipe> recipes;
}
