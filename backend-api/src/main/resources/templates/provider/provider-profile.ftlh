<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Provider Profile</title>
    <link rel="stylesheet" href="/providerStyle.css">
</head>
<body class="dashboard">
    <nav class="nav">
        <a href="/provider/dashboard"><button>Dashboard</button></a>
        <a href="/provider/uploads"><button>Uploads</button></a>
        <a href="/provider/profile"><button>Profile</button></a>
        <a href="/provider/login"><button>Log Out</button></a>
    </nav>

    <main class="main">
        <!-- Profile header: avatar, name, rating, edit -->
        <div class="profile-header">
            <div class="avatar">
                <img class="avatar-placeholder" src="/provider/picture/${provider.id}">
            </div>
            <div class="profile-identity">
                <h1>${provider.name}</h1>
                <div class="rating">
                    <span class="stars">&#9733;&#9733;&#9733;&#9733;&#9734;</span>
                    <span class="rating-value">4.2 / 5</span>
                    <span class="rating-count">(128 reviews)</span>
                </div>
            </div>
            <a href="/provider/profile/edit"><button class="edit-btn">Edit Profile</button></a>
        </div>

        <!-- Mission statement -->
        <div class="profile-section">
            <h4>Description</h4>
            <div class="profile-card">
                <p>${provider.bio!"No description yet"}</p>
            </div>
        </div>

        <!-- Specialties / tags -->
        <div class="profile-section">
            <h4>Specialties</h4>
            <div class="profile-card">
                <div class="tag-list">
                    <#if provider.specialties?? && provider.specialties?has_content>
                        <#list provider.specialties as specialty>
                            <span class="tag">${specialty}</span>
                        </#list>
                    <#else>
                        <span class="tag-empty">No specialties listed</span>
                    </#if>
                </div>
            </div>
        </div>

        <!-- Content -->
        <div class="profile-section">
            <div class="section-head">
                <h4>Content</h4>
                <button>View All</button>
            </div>
            <div class="profile-card">
                <div class="content-grid">
                    <div class="content-item">
                        <div class="content-thumb"></div>
                        <p>Recipe</p>
                    </div>
                    <div class="content-item">
                        <div class="content-thumb"></div>
                        <p>Mealplan</p>
                    </div>
                    <div class="content-item">
                        <div class="content-thumb"></div>
                        <p>Recipe</p>
                    </div>
                    <div class="content-item">
                        <div class="content-thumb"></div>
                        <p>Meal Kit</p>
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
