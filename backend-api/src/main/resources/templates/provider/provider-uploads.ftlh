<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Provider Uploads</title>
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
        <div class="header-bar">
            <h1>Uploads</h1>
            <a href="/provider/uploads/create"><button class="create-btn">Create</button></a>
        </div>

        <div class="panels">
            <div class="panel-section">
                <div class="panel">
                    <div class="upload-list upload-list-full">
                        <#if uploads?has_content>
                            <#list uploads as upload>
                                <div class="upload-item upload-card">
                                    <div class="upload-info">
                                        <span class="upload-type">${upload.type}</span>
                                        <p class="upload-title">${upload.title}</p>
                                    </div>
                                    <div class="tag-list">
                                        <#if upload.tags?has_content>
                                            <#list upload.tags as tag>
                                                <span class="tag">${tag}</span>
                                            </#list>
                                        <#else>
                                            <span class="tag-empty">No tags</span>
                                        </#if>
                                    </div>
                                    <div class="upload-actions">
                                        <button>view</button>
                                        <form method="post"
                                              action="/provider/uploads/${upload.type?lower_case}/${upload.id?c}/delete"
                                              onsubmit="return confirm('Delete &quot;${upload.title?js_string}&quot;? Saves, reviews and subscriptions for it are removed too. This cannot be undone.');">
                                            <button type="submit" class="delete-btn">Delete</button>
                                        </form>
                                    </div>
                                </div>
                            </#list>
                        <#else>
                            <div class="upload-item upload-card">
                                <p>No uploads yet</p>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>