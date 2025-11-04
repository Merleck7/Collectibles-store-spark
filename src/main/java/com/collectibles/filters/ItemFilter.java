package com.collectibles.filters;

import com.collectibles.models.Item;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to apply filters to collectible items.
 * Sprint 2 - Filters by description and price range.
 */
public class ItemFilter {

    // Filter items by keyword in the description
    public static List<Item> filterByDescription(List<Item> items, String keyword) {
        return items.stream()
                .filter(item -> item.getDescription() != null &&
                        item.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Filter items by price range
    public static List<Item> filterByPriceRange(List<Item> items, double min, double max) {
        return items.stream()
                .filter(item -> item.getPrice() >= min && item.getPrice() <= max)
                .collect(Collectors.toList());
    }
}
