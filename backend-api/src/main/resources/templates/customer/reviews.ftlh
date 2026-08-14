<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Reviews</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#include "_navbar.ftlh">

    <div class="dashboard-container">
        <h3>My Reviews</h3>
        <p>Review your saved recipes and meal plans, and the meal kits you subscribe to.</p>

        <div class="card-grid">
            <#list reviewCards as rc>
                <div class="service-card" data-id="${rc.content().id()?c}">
                    <div class="card-body">
                        <h4>${rc.content().title()}</h4>
                        <p class="provider-name">by ${rc.content().providerName()}</p>
                        <div class="card-tags">
                            <span class="tag-pill small type-pill">${rc.content().type()?cap_first}</span>
                        </div>
                    </div>
                    <form class="review-form" method="post" action="/customer/${customerId?c}/reviews/${rc.content().type()}/${rc.content().id()?c}">
                        <label>Your Rating</label>
                        <select class="review-rating" name="rating">
                            <option value="5" <#if rc.existingRating()?? && rc.existingRating() == 5>selected</#if>>5 - Excellent</option>
                            <option value="4" <#if rc.existingRating()?? && rc.existingRating() == 4>selected</#if>>4 - Good</option>
                            <option value="3" <#if rc.existingRating()?? && rc.existingRating() == 3>selected</#if>>3 - Okay</option>
                            <option value="2" <#if rc.existingRating()?? && rc.existingRating() == 2>selected</#if>>2 - Poor</option>
                            <option value="1" <#if rc.existingRating()?? && rc.existingRating() == 1>selected</#if>>1 - Terrible</option>
                        </select>
                        <label>Your Review</label>
                        <textarea class="review-comment" name="comment" placeholder="What did you think?">${(rc.existingComment())!}</textarea>
                        <button type="submit" class="subscribe-btn review-save-btn">
                            <#if rc.existingRating()??>Update Review<#else>Save Review</#if>
                        </button>
                    </form>
                </div>
            </#list>
        </div>

        <#if reviewCards?size == 0>
            <p class="no-subscriptions">Save a recipe or meal plan, or subscribe to a meal kit, to review it. <a href="/customer/${customerId?c}/dashboard">Browse content</a></p>
        </#if>
    </div>
</body>
</html>
