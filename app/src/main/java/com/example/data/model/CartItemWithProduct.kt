package com.example.data.model

import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.ProductEntity

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)
