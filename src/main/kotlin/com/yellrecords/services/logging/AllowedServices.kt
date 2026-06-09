package com.yellrecords.services.logging

/**
 * Helper class for logging to identify valid service names.
 *
 * @see names
 */
object AllowedServices {
    /** Group of strings which identifies valid API service routes. */
    val names =
        setOf(
            "artist-pages",
            "auth",
            "cart-items",
            "categories",
            "images",
            "item-listings",
            "orders",
            "policies",
            "users",
        )
}
