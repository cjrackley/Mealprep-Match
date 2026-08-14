<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Saved</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#include "_navbar.ftlh">
    <#import "_content_card.ftlh" as cards>
    <#assign redirect = "/customer/" + customerId?c + "/saved">

    <div class="dashboard-container">
        <h3>Saved</h3>
        <p>Recipes and meal plans you have saved.</p>

        <div class="card-grid">
            <#list savedCards as card>
                <@cards.contentCard card=card customerId=customerId redirect=redirect />
            </#list>
        </div>

        <#if savedCards?size == 0>
            <p class="no-subscriptions">You haven't saved anything yet. <a href="/customer/${customerId?c}/dashboard">Browse content</a></p>
        </#if>
    </div>
</body>
</html>
