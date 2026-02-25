package com.example.onlinebookstore

import java.io.Serializable

data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val year: String,
    val publisher: String,
    val imageUrlS: String,
    val imageUrlM: String,
    val imageUrlL: String,
    var price: Double = 0.0,
    var oldPrice: Double = 0.0, // For discount display
    var averageRating: Double = 0.0,
    var ratingsCount: Int = 0,
    var inStock: Boolean = true,
    var binding: String = "Paperback"
) : Serializable
