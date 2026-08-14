<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${provider.name}'s Uploads</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#include "_navbar.ftlh">
    <#import "_content_card.ftlh" as cards>
    <#assign redirect = "/customer/" + customerId?c + "/providers/" + provider.id?c + "/uploads">

    <div class="dashboard-container">
        <h3>${provider.name}'s Uploads</h3>
        <p><a href="/customer/${customerId?c}/providers/${provider.id?c}">&larr; Back to profile</a></p>

        <div class="card-grid">
            <#list uploads as card>
                <@cards.contentCard card=card customerId=customerId redirect=redirect />
            </#list>
        </div>

        <#if uploads?size == 0>
            <p class="no-results">${provider.name} hasn't uploaded anything yet.</p>
        </#if>
    </div>
</body>
</html>
