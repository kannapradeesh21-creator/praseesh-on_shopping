package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val weightOrSize: String,
    val stockQuantity: Int,
    val imageUrl: String,
    val isDeal: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false,
    val rating: Float = 4.8f,
    val reviewsCount: Int = 120
)
