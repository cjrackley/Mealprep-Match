<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${provider.name}</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#include "_navbar.ftlh">

    <div class="dashboard-container">
        <div class="profile-card provider-public-profile">
            <div class="provider-header">
                <img class="provider-avatar large" src="/provider/picture/${provider.id?c}" alt="${provider.name}">
                <div>
                    <h3>${provider.name}</h3>
                    <#if provider.verified?? && provider.verified>
                        <span class="verified-badge">Verified</span>
                    </#if>
                </div>
            </div>

            <#if provider.specialties?? && provider.specialties?size gt 0>
                <label>Specialties</label>
                <div class="card-tags">
                    <#list provider.specialties as specialty>
                        <span class="tag-pill small">${specialty}</span>
                    </#list>
                </div>
            </#if>

            <#if provider.bio?? && provider.bio?length gt 0>
                <label>About</label>
                <p class="card-description">${provider.bio}</p>
            </#if>

            <label>Stats</label>
            <#if stats.reviewCount gt 0>
                <p class="rating">${stats.averageRating?string("0.0")} average rating (${stats.reviewCount} reviews)</p>
            <#else>
                <p class="rating">No reviews yet</p>
            </#if>
            <p class="rating">${stats.contentCount} uploads &middot; ${stats.subscriptionCount} subscribers</p>

            <a class="subscribe-btn card-link" href="/customer/${customerId?c}/providers/${provider.id?c}/uploads">View Uploads</a>
        </div>
    </div>
</body>
</html>
