<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Find Providers</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#include "_navbar.ftlh">

    <div class="dashboard-container">
        <h3>Find Providers</h3>
        <p>Search providers by specialty, or browse them all.</p>

        <form class="search-bar" method="get" action="/customer/${customerId?c}/providers">
            <input type="text" name="query" placeholder="e.g. vegan, high protein, italian" value="${query}">
            <button type="submit" class="register-btn">Search</button>
        </form>

        <div class="card-grid">
            <#list providers as p>
                <div class="service-card">
                    <div class="card-body">
                        <div class="provider-header">
                            <img class="provider-avatar" src="/provider/picture/${p.id?c}" alt="${p.name}">
                            <div>
                                <h4>${p.name}</h4>
                                <#if p.verified?? && p.verified>
                                    <span class="verified-badge">Verified</span>
                                </#if>
                            </div>
                        </div>
                        <#if p.specialties?? && p.specialties?size gt 0>
                            <div class="card-tags">
                                <#list p.specialties as specialty>
                                    <span class="tag-pill small">${specialty}</span>
                                </#list>
                            </div>
                        </#if>
                        <#if p.bio?? && p.bio?length gt 0>
                            <p class="card-description">${p.bio}</p>
                        </#if>
                    </div>
                    <a class="subscribe-btn card-link" href="/customer/${customerId?c}/providers/${p.id?c}">View Profile</a>
                </div>
            </#list>
        </div>

        <#if providers?size == 0>
            <p class="no-results">No providers found<#if query?length gt 0> for "${query}"</#if>.</p>
        </#if>
    </div>
</body>
</html>
