<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Dashboard</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#include "_navbar.ftlh">
    <#import "_content_card.ftlh" as cards>
    <#assign redirect = "/customer/" + customerId?c + "/dashboard">

    <div class="dashboard-container">
        <h3>Welcome back!!</h3>

        <div class="filter-tags">
            <span class="tag-pill active" data-filter="All">All</span>
            <#assign seenTags = []>
            <#list recipes + mealplans + mealkits as card>
                <#list card.tags() as tag>
                    <#if !seenTags?seq_contains(tag?lower_case)>
                        <#assign seenTags = seenTags + [tag?lower_case]>
                        <span class="tag-pill" data-filter="${tag}">${tag}</span>
                    </#if>
                </#list>
            </#list>
        </div>

        <h4 class="section-title">Recipes</h4>
        <div class="card-grid">
            <#list recipes as card>
                <@cards.contentCard card=card customerId=customerId redirect=redirect />
            </#list>
        </div>
        <#if recipes?size == 0>
            <p class="no-results">No recipes are available yet.</p>
        </#if>

        <h4 class="section-title">Meal Plans</h4>
        <div class="card-grid">
            <#list mealplans as card>
                <@cards.contentCard card=card customerId=customerId redirect=redirect />
            </#list>
        </div>
        <#if mealplans?size == 0>
            <p class="no-results">No meal plans are available yet.</p>
        </#if>

        <h4 class="section-title">Meal Kits</h4>
        <div class="card-grid">
            <#list mealkits as card>
                <@cards.contentCard card=card customerId=customerId redirect=redirect />
            </#list>
        </div>
        <#if mealkits?size == 0>
            <p class="no-results">No meal kits are available yet.</p>
        </#if>

        <p class="no-results" hidden>No content matches this filter yet.</p>
    </div>

    <script>
        const filterTags = document.querySelectorAll('.filter-tags .tag-pill');
        const cards = document.querySelectorAll('.service-card');
        const noResults = document.querySelector('p.no-results[hidden]');

        filterTags.forEach(tag => {
            tag.addEventListener('click', () => {
                filterTags.forEach(t => t.classList.remove('active'));
                tag.classList.add('active');

                const filter = tag.dataset.filter;
                let visibleCount = 0;

                cards.forEach(card => {
                    const matches = filter === 'All'
                        || card.dataset.tags.toLowerCase().split(' ').includes(filter.toLowerCase());
                    card.hidden = !matches;
                    if (matches) visibleCount++;
                });

                if (noResults) noResults.hidden = visibleCount > 0;
            });
        });
    </script>
</body>
</html>
