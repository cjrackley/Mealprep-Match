<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Upload</title>
    <link rel="stylesheet" href="/providerStyle.css">
</head>
<body class="dashboard">
    <nav class="nav">
        <a href="/provider/dashboard"><button>Dashboard</button></a>
        <a href="/provider/uploads"><button>Uploads</button></a>
        <a href="/provider/profile"><button>Profile</button></a>
        <a href="/provider/login"><button>Log Out</button></a>
    </nav>

    <#-- Checkbox list of this provider's recipes, shared by the mealplan and mealkit forms. -->
    <#macro recipePicker formId>
        <div class="profile-section">
            <h4>Recipes</h4>
            <div class="profile-card">
                <#if recipes?has_content>
                    <div class="recipe-picker">
                        <#list recipes as recipe>
                            <label class="recipe-option">
                                <input type="checkbox" name="recipeIds" value="${recipe.id?c}"
                                       id="${formId}-recipe-${recipe.id?c}">
                                <span>${recipe.title}</span>
                            </label>
                        </#list>
                    </div>
                    <small>Pick the recipes this includes. You can add more later.</small>
                <#else>
                    <small>You have no recipes yet. Create a recipe first to include it here.</small>
                </#if>
            </div>
        </div>
    </#macro>

    <main class="main">
        <div class="header-bar">
            <h1>Create Upload</h1>
        </div>

        <div class="type-picker">
            <label><input type="radio" name="uploadType" value="recipe" checked><span>Recipe</span></label>
            <label><input type="radio" name="uploadType" value="mealplan"><span>Mealplan</span></label>
            <label><input type="radio" name="uploadType" value="mealkit"><span>Mealkit</span></label>
        </div>

        <!-- Recipe form -->
        <form id="recipe-form" action="/provider/uploads/create/recipe" method="post">
            <div class="profile-section">
                <h4>Title</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="title" placeholder="e.g. Garlic Butter Salmon" required>
                </div>
            </div>

            <div class="profile-section">
                <h4>Ingredients</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="ingredients"
                        placeholder="e.g. salmon fillet, butter, garlic, lemon">
                    <small>Separate ingredients with commas.</small>
                </div>
            </div>

            <div class="profile-section">
                <h4>Instructions</h4>
                <div class="profile-card">
                    <textarea class="profile-input" name="instructions" rows="6"
                        placeholder="Describe the steps to make this recipe..."></textarea>
                </div>
            </div>

            <div class="profile-section">
                <h4>Tags</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="tags"
                        placeholder="e.g. high protein, quick, gluten free">
                    <small>Separate tags with commas. Leave blank if you have none.</small>
                </div>
            </div>

            <button type="submit" class="create-btn">Create Recipe</button>
        </form>

        <!-- Mealplan form: free content, so no price. -->
        <form id="mealplan-form" action="/provider/uploads/create/mealplan" method="post" hidden>
            <div class="profile-section">
                <h4>Title</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="title" placeholder="e.g. 7-Day Mediterranean Reset" required>
                </div>
            </div>

            <div class="profile-section">
                <h4>Duration</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="duration" placeholder="e.g. 7 days" required>
                </div>
            </div>

            <div class="profile-section">
                <h4>Description</h4>
                <div class="profile-card">
                    <textarea class="profile-input" name="description" rows="4"
                        placeholder="Describe what this mealplan includes..."></textarea>
                </div>
            </div>

            <div class="profile-section">
                <h4>Schedule</h4>
                <div class="profile-card">
                    <textarea class="profile-input" name="schedule" rows="6"
                        placeholder="e.g. Mon: overnight oats / grain bowl / salmon&#10;Tue: ..."></textarea>
                    <small>Free text. Lay out the week however makes sense for this plan.</small>
                </div>
            </div>

            <@recipePicker formId="mealplan" />

            <div class="profile-section">
                <h4>Category</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="category" placeholder="e.g. Mediterranean">
                </div>
            </div>

            <button type="submit" class="create-btn">Create Mealplan</button>
        </form>

        <!-- Mealkit form: the paid content type. -->
        <form id="mealkit-form" action="/provider/uploads/create/mealkit" method="post" hidden>
            <div class="profile-section">
                <h4>Title</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="title" placeholder="e.g. Weeknight Stir-Fry Kit" required>
                </div>
            </div>

            <div class="profile-section">
                <h4>Duration</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="duration" placeholder="e.g. 1 week" required>
                </div>
            </div>

            <div class="profile-section">
                <h4>Description</h4>
                <div class="profile-card">
                    <textarea class="profile-input" name="description" rows="4"
                        placeholder="Describe what comes in this mealkit..." required></textarea>
                </div>
            </div>

            <@recipePicker formId="mealkit" />

            <div class="profile-section">
                <h4>Kit Ingredients</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="ingredients"
                        placeholder="e.g. 400g chicken thigh, 2 bell peppers, stir-fry sauce">
                    <small>Separate ingredients with commas. This is what actually ships in the box, so
                        write quantities the way you want them packed and leave out pantry staples.</small>
                </div>
            </div>

            <div class="profile-section">
                <h4>Category</h4>
                <div class="profile-card">
                    <input type="text" class="profile-input" name="category" placeholder="e.g. Asian Fusion">
                </div>
            </div>

            <div class="profile-section">
                <h4>Price</h4>
                <div class="profile-card">
                    <input type="number" class="profile-input" name="price" min="0" step="0.01" placeholder="e.g. 49.99">
                </div>
            </div>

            <button type="submit" class="create-btn">Create Mealkit</button>
        </form>
    </main>

    <script src="/providerUpload.js"></script>
</body>
</html>
