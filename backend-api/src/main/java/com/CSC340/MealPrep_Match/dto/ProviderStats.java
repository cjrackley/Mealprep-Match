package com.CSC340.MealPrep_Match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProviderStats {

    private double averageRating;
    private long reviewCount;
    private long subscriptionCount;
    private long contentCount;
    private long saveCount;
    private double totalRevenue;
    /** Revenue from subscriptions started in the current calendar month (UTC). */
    private double monthlyRevenue;
}
