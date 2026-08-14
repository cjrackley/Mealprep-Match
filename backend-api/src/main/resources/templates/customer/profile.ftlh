<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Profile</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body class="dashboard-body">
    <#assign customerId = customer.id>
    <#include "_navbar.ftlh">

    <div class="dashboard-container">
        <h3>My Profile</h3>

        <div class="profile-card">
            <form method="post" action="/customer/${customerId?c}/profile">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" value="${customer.name}">

                <label for="email">Email</label>
                <input type="text" id="email" name="email" value="${customer.email}">

                <label for="dietaryPreferences">Dietary Preferences (comma separated)</label>
                <input type="text" id="dietaryPreferences" name="dietaryPreferences"
                       value="<#if customer.dietaryPreferences??>${customer.dietaryPreferences?join(', ')}</#if>">

                <button type="submit" class="register-btn">Save Profile</button>
            </form>
        </div>
    </div>
</body>
</html>
