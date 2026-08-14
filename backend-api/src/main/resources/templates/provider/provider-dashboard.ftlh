<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Provider Dashboard</title>
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
            <h1>Dashboard</h1>
            <a href="/provider/uploads"><button class="create-btn">Create</button></a>
        </div>

        <div class="stats-section">
            <h4>Statistics</h4>
            <div class="stats">
                <div class="stat">
                    <h4>Total Uploads</h4>
                    <p>${stats.contentCount}</p>
                </div>

                <div class="stat">
                    <h4>Average Rating</h4>
                    <p>${stats.averageRating?string("0.0")} / 5</p>
                </div>

                <div class="stat">
                    <h4>Reviews</h4>
                    <p>${stats.reviewCount}</p>
                </div>

                <div class="stat">
                    <h4>Saves</h4>
                    <p>${stats.saveCount}</p>
                </div>
            </div>
        </div>

        <div class="panels">
            <div class="panel-section">
                <h4>Recent Uploads</h4>
                <div class="panel">
                    <div class="panel-head">
                        <a href="/provider/uploads"><button>View All</button></a>
                    </div>
                    <div class="upload-list">
                        <#if recentUploads?has_content>
                            <#list recentUploads as upload>
                                <div class="upload-item">
                                    <p>${upload.type} - ${upload.title}</p>
                                    <button>view</button>
                                </div>
                            </#list>
                        <#else>
                            <div class="upload-item">
                                <p>No uploads yet</p>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>

            <div class="panel-section">
                <h4>Recent Reviews</h4>
                <div class="panel">
                    <div class="panel-head">
                        <button>View All</button>
                    </div>
                    <div class="upload-list">
                        <#if recentReviews?has_content>
                            <#list recentReviews as review>
                                <div class="upload-item">
                                    <p>${review.rating}/5 - ${review.comment!''}</p>
                                    <a href="#review-modal-${review.id}"><button>view</button></a>
                                </div>
                            </#list>
                        <#else>
                            <div class="upload-item">
                                <p>No reviews yet</p>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>
        </div>

        <div class="stats-section">
            <h4>Subscription Statistics</h4>
            <div class="stats">
                <div class="stat">
                    <h4>Subscriptions</h4>
                    <p>${stats.subscriptionCount}</p>
                </div>

                <div class="stat">
                    <h4>This Month's Revenue</h4>
                    <p>$${stats.monthlyRevenue?string("0.00")}</p>
                </div>

                <div class="stat">
                    <h4>Total Revenue</h4>
                    <p>$${stats.totalRevenue?string("0.00")}</p>
                </div>

            </div>
        </div>
    </main>

    <!-- Review reply modals -->
    <#if recentReviews?has_content>
        <#list recentReviews as review>
            <div class="modal" id="review-modal-${review.id}">
                <a href="#" class="modal-backdrop"></a>
                <div class="modal-card">
                    <a href="#" class="modal-close">&times;</a>
                    <div class="rating">
                        <span class="stars"><#list 1..review.rating as star>&#9733;</#list></span>
                        <span class="rating-value">${review.rating} / 5</span>
                    </div>
                    <p class="review-text">${review.comment!''}</p>
                    <form method="post" action="/provider/reviews/${review.id}/reply">
                        <label for="reply-${review.id}">Reply</label>
                        <textarea class="profile-input reply-box" id="reply-${review.id}"
                                  name="reply" rows="4"
                                  placeholder="Write a reply...">${review.providerReply!''}</textarea>
                        <div class="modal-actions">
                            <a href="#" class="modal-cancel">Cancel</a>
                            <button type="submit" class="edit-btn">Send</button>
                        </div>
                    </form>
                </div>
            </div>
        </#list>
    </#if>
</body>
</html>