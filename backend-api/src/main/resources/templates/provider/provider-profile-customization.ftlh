<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Provider Profile Customization</title>
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
        <form method="post" action="/provider/profile/edit" enctype="multipart/form-data">

            <!-- Profile header: avatar, name, save -->
            <div class="profile-header">
                <div class="avatar">
                    <img class="avatar-placeholder" src="/provider/picture/${provider.id}">
                </div>
                <div class="profile-identity">
                    <label for="name">Display Name</label>
                    <input type="text" class="profile-input" id="name" name="name" value="${provider.name!''}" required>

                    <label for="profilePictureFile">Profile Picture</label>
                    <input type="file" class="file-input" id="profilePictureFile" name="profilePictureFile" accept="image/*">
                </div>
                <button type="submit" class="edit-btn">Save Profile</button>
            </div>

            <!-- Mission statement -->
            <div class="profile-section">
                <h4>Description</h4>
                <div class="profile-card">
                    <textarea class="profile-input" id="bio" name="bio" rows="4"
                        placeholder="Tell customers what you're about and the types of items you make...">${provider.bio!''}</textarea>
                </div>
            </div>

            <!-- Specialties -->
            <div class="profile-section">
                <h4>Specialties</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" id="specialties" name="specialties"
                        value="<#if provider.specialties??>${provider.specialties?join(', ')}</#if>"
                        placeholder="e.g. Vegan Friendly, Meal Plans, Recipes">
                    <small>Separate specialties with commas. Leave blank if you have none.</small>
                </div>
            </div>

        </form>
    </main>
</body>
</html>
