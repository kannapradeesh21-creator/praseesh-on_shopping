package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val city: String,
    val paymentMethod: String,
    val orderStatus: String, // "Pending", "Processing", "Shipped", "Delivered", "Cancelled"
    val totalPrice: Double,
    val itemsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
