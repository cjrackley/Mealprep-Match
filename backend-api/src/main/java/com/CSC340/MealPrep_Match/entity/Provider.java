package com.CSC340.MealPrep_Match.entity;

import java.sql.Blob;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private String password;

    @Lob
    private Blob profilePicture;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection
    @CollectionTable(name = "provider_specialties", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "specialty")
    private List<String> specialties;

    private Boolean verified;

    @OneToMany(mappedBy = "provider")
    @JsonIgnoreProperties({ "provider" })
    private List<Recipe> recipes;

    @OneToMany(mappedBy = "provider")
    @JsonIgnoreProperties({ "provider" })
    private List<Mealplan> mealplans;

    @OneToMany(mappedBy = "provider")
    @JsonIgnoreProperties({ "provider" })
    private List<Mealkit> mealkits;
}
