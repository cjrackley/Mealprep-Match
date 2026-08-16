
# Requirements – Starter Template

**Project Name:** MealPrep Match \
**Team:** Cameron - Provider, Victor - Customer \
**Course:** CSC 340\
**Version:** 2.0\
**Date:** 2026-07-23

---

## 1. Overview
**Vision.** One or two sentences: This is an app designed to help people who want healthy, structured meal plans. It can be used for people who want to subscribe to mealkits and those who just want extra recipes to try.

**Glossary** Terms used in the project
- **Provider:** Anyone who wants to upload recipes, mealplans or mealkits.
- **Customer:** A person seeking structure and or guidance in food habits and meals.
- **Profile:** Information about a user or provider including personal details, preferences, account info
- **Subscription(s):** A specific piece of content provided by a verified provider. Specifically for ordering mealkits
- **Recipe:** A set of instructions and a list of required ingredients to make a meal
- **Mealplan:** A list of meals/recipes to make for a set amount of times(a day, a week, a month).
- **Mealkit:** A set of ingredients to be delivered to a subscribing customer's location. Ingredients go with specified recified, preset recipes.

**Primary Users / Roles.**
- **Customer (e.g., Student/Patient/Pet Owner/etc. )** — Find good recipes or stable meal plans.
- **Provider (e.g., Teacher/Doctor/Pet Sitter/etc. )** — Attract customers and manage subscriptions.

**Scope (this semester).**
- Customer & Provider profiles
- Browse and search by tags like recipe, mealkit or vegan, carnivore
- Basic Mealkit/mealprep/recipe uploads
- Basic statistic tracking
- Reviews and Ratings

**Out of scope (deferred).**
- Advanced Mealkit/mealprep/recipe uploads
- <deferred 2>

> This document is **requirements‑level** and solution‑neutral; design decisions (UI layouts, API endpoints, schemas) are documented separately.

---

## 2. Functional Requirements (User Stories)
Write each story as: **As a `<role>`, I want `<capability>`, so that `<benefit>`.** Each story includes at least one **Given/When/Then** scenario.

### 2.1 Customer Stories
- **US‑1 — Create Customer Profile**  
  _Story:_ As a customer, I want to create a profile with my dietary preferences and goals, so that I can be shown services that match my needs.  
  _Acceptance:_
  ```gherkin
  Scenario: Customer registers and sets preferences
    Given I am on the registration page and select the Customer role
    When  I fill in my name, email, password, dietary preferences, and health goals
    Then  my profile is saved and I am directed to the browse screen filtered by my preferences
  ```

- **US‑2 — Browse and Filter Services**  
  _Story:_ As a customer, I want to filter available recipes, mealkits, and mealprep guides by preference tags, so that I only see content relevant to my diet.  
  _Acceptance:_
  ```gherkin
  Scenario: Customer filters by tag
    Given I am on the browse screen and services exist with tags (vegan, carnivore, etc.)
    When  I select the "vegan" filter tag
    Then  only services tagged as vegan are displayed
  ```

- **US‑3 — Subscribe to a Service**  
  _Story:_ As a customer, I want to select a provider and subscribe to one of their meal plans, so that I can access their paid content.  
  _Acceptance:_
  ```gherkin
  Scenario: Customer subscribes to a meal plan
    Given I am viewing a provider's paid mealkit and I have an account
    When  I click Subscribe and complete the payment flow
    Then  the mealkit is added to my active subscriptions and I can access its full content
  ```

- **US‑4 — Write a Review**  
  _Story:_ As a customer, I want to submit a written review for a service I have subscribed to, so that I can share feedback with other customers and the provider.  
  _Acceptance:_
  ```gherkin
  Scenario: Customer submits a review
    Given I have an active or past subscription to a service
    When  I navigate to that service and submit a review with written feedback
    Then  the review is posted publicly on the service page and the provider is notified
  ```

### 2.2 Provider Stories
- **US-20 — Create Provider Profile**  
  _Story:_ As a provider, I want to register and tag my profile with the types of content I offer, so that customers with matching preferences can find me.  
  _Acceptance:_
  ```gherkin
  Scenario: Provider registers and adds tags
    Given I am on the registration page and select the Provider role
    When  I fill in my profile details and select tags (e.g., vegan, mealprep)
    Then  my profile can be used for customers to find my content related to my tags.
  ```

- **US-21 — Publish a Service**  
  _Story:_ As a provider, I want to publish a recipe, mealkit, or mealprep guide with pricing, so that customers can discover and subscribe to my content.  
  _Acceptance:_
  ```gherkin
  Scenario: Provider publishes a mealkit
    Given I am a verified provider on my dashboard
    When  I create a new Mealkit, Mealplan or Recipe
    Then  the mealkit/mealplan/recipe is published and visible to customers on the browse screen
  ```

- **US-22 — View Service Statistics**  
  _Story:_ As a provider, I want to see active subscriber counts, overall ratings, and saves, so that I can understand how my content is performing.  
  _Acceptance:_
  ```gherkin
  Scenario: Provider views dashboard stats
    Given I have at least one published service with customer interactions
    When  I open my provider dashboard
    Then  I see active subscriber count, average rating, and saves for my services
  ```

- **US-23 — Reply to a Review**  
  _Story:_ As a provider, I want to reply to customer reviews on my services, so that I can address feedback and engage with my subscribers.  
  _Acceptance:_
  ```gherkin
  Scenario: Provider replies to a review
    Given a customer has posted a review on one of my services
    When  I navigate to that review and submit a reply
    Then  my reply is displayed beneath the customer's review
  ```

### 2.3 SysAdmin Stories
- **US‑30 — <short title>**  
  _Story:_ As a sysadmin, I want … so that …  
  _Acceptance:_
  ```gherkin
  Scenario: <happy path>
    Given <preconditions>
    When  <action>
    Then  <observable outcome>
  ```

- **US‑31 — <short title>**  
  _Story:_ As a sysadmin, I want … so that …  
  _Acceptance:_
  ```gherkin
  Scenario: <happy path>
    Given <preconditions>
    When  <action>
    Then  <observable outcome>
  ```

---

## 3. Non‑Functional Requirements (make them measurable)
- **Performance:** description 
- **Availability/Reliability:** description
- **Security/Privacy:** description
- **Usability:** description

---

## 4. Assumptions, Constraints, and Policies
- list any rules, policies, assumptions, etc.

---

## 5. Milestones (course‑aligned)
- **M0 Requirements** — this file + stories opened as issues. 
- **M1 High‑fidelity prototype** — core customer/provider flows fully interactive. 
- **M2 Backend API** — key endpoints + tests. 
- **M3 Final** — complete system & documentation. 

---

## 6. Change Management
- Stories are living artifacts; changes are tracked via repository issues and linked pull requests.  
- Major changes should update this SRS.
