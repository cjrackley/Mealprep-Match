<#-- Renders one ContentCard (recipe, mealplan, or mealkit) with its save/subscribe action. -->
<#macro contentCard card customerId redirect>
<div class="service-card" data-id="${card.id()?c}" data-tags="${card.type()} <#list card.tags() as tag>${tag}<#sep> </#sep></#list>">
    <div class="card-body">
        <h4>${card.title()}</h4>
        <p class="provider-name">by
            <#if card.providerId()??>
                <a href="/customer/${customerId?c}/providers/${card.providerId()?c}">${card.providerName()}</a>
            <#else>
                ${card.providerName()}
            </#if>
        </p>
        <div class="card-tags">
            <span class="tag-pill small type-pill">${card.type()?cap_first}</span>
            <#list card.tags() as tag>
                <span class="tag-pill small">${tag}</span>
            </#list>
        </div>
        <#if card.price()??>
            <p class="price">$${card.price()?string("0.00")}<#if card.duration()?? && card.duration()?length gt 0> &middot; ${card.duration()}</#if></p>
        <#elseif card.duration()?? && card.duration()?length gt 0>
            <p class="price">${card.duration()}</p>
        </#if>
        <#if card.reviewCount() gt 0>
            <p class="rating">${card.averageRating()?string("0.0")} (${card.reviewCount()} reviews)</p>
        <#else>
            <p class="rating">No reviews yet</p>
        </#if>
        <#if card.description()?? && card.description()?length gt 0>
            <p class="card-description">${card.description()}</p>
        </#if>
        <#if card.schedule()?? && card.schedule()?length gt 0>
            <p class="mealkit-list-label">Schedule:</p>
            <p class="card-description">${card.schedule()}</p>
        </#if>
        <#if (card.ingredients()![])?size gt 0>
            <p class="mealkit-list-label">Ingredients:</p>
            <ul class="mealkit-list">
                <#list card.ingredients() as ingredient>
                    <li>${ingredient}</li>
                </#list>
            </ul>
        </#if>
        <#if (card.recipeTitles()![])?size gt 0>
            <p class="mealkit-list-label">Recipes:</p>
            <ul class="mealkit-list">
                <#list card.recipeTitles() as recipeTitle>
                    <li>${recipeTitle}</li>
                </#list>
            </ul>
        </#if>
    </div>
    <#if card.type() == "mealkit">
        <#if card.actioned()>
            <form method="post" action="/customer/${customerId?c}/unsubscribe/${card.id()?c}">
                <input type="hidden" name="redirect" value="${redirect}">
                <button type="submit" class="subscribe-btn subscribed">Unsubscribe</button>
            </form>
        <#else>
            <form method="post" action="/customer/${customerId?c}/subscribe/${card.id()?c}">
                <input type="hidden" name="redirect" value="${redirect}">
                <button type="submit" class="subscribe-btn">Subscribe</button>
            </form>
        </#if>
    <#else>
        <#if card.actioned()>
            <form method="post" action="/customer/${customerId?c}/unsave/${card.type()}/${card.id()?c}">
                <input type="hidden" name="redirect" value="${redirect}">
                <button type="submit" class="subscribe-btn subscribed">Unsave</button>
            </form>
        <#else>
            <form method="post" action="/customer/${customerId?c}/save/${card.type()}/${card.id()?c}">
                <input type="hidden" name="redirect" value="${redirect}">
                <button type="submit" class="subscribe-btn">Save</button>
            </form>
        </#if>
    </#if>
</div>
</#macro>
