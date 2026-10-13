package com.example.data.repository

import com.example.data.local.SampleData
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.model.CartItemWithProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class ShopRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val deals: Flow<List<ProductEntity>> = productDao.getDeals()
    val bestSellers: Flow<List<ProductEntity>> = productDao.getBestSellers()
    val newArrivals: Flow<List<ProductEntity>> = productDao.getNewArrivals()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun checkAndSeedInitialProducts() {
        if (productDao.getProductCount() == 0) {
            productDao.insertAll(SampleData.initialProducts)
        }
    }

    fun getProductById(id: Long): Flow<ProductEntity?> = productDao.getProductById(id)

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
        return if (category == "All") {
            productDao.getAllProducts()
        } else {
            productDao.getProductsByCategory(category)
        }
    }

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return productDao.searchProducts(query)
    }

    suspend fun insertProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    // Cart operations
    val cartItemsWithProducts: Flow<List<CartItemWithProduct>> =
        combine(cartDao.getAllCartItems(), productDao.getAllProducts()) { cartItems, products ->
            val productMap = products.associateBy { it.id }
            cartItems.mapNotNull { cartItem ->
                productMap[cartItem.productId]?.let { product ->
                    CartItemWithProduct(cartItem, product)
                }
            }
        }

    suspend fun addToCart(productId: Long, selectedOption: String, quantityToAdd: Int = 1) {
        val existing = cartDao.getCartItemByProduct(productId)
        if (existing != null) {
            cartDao.updateCartItem(
                existing.copy(
                    quantity = existing.quantity + quantityToAdd,
                    selectedOption = if (selectedOption.isNotBlank()) selectedOption else existing.selectedOption
                )
            )
        } else {
            cartDao.insertCartItem(
                CartItemEntity(
                    productId = productId,
                    quantity = quantityToAdd,
                    selectedOption = selectedOption
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        val items = cartDao.getAllCartItems().firstOrNull() ?: return
        val item = items.find { it.id == cartItemId } ?: return
        if (newQuantity <= 0) {
            cartDao.deleteCartItem(item)
        } else {
            cartDao.updateCartItem(item.copy(quantity = newQuantity))
        }
    }

    suspend fun removeCartItem(cartItemId: Long) {
        val items = cartDao.getAllCartItems().firstOrNull() ?: return
        val item = items.find { it.id == cartItemId } ?: return
        cartDao.deleteCartItem(item)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Orders
    suspend fun placeOrder(order: OrderEntity): Long {
        val id = orderDao.insertOrder(order)
        clearCart()
        return id
    }

    suspend fun updateOrderStatus(order: OrderEntity, newStatus: String) {
        orderDao.updateOrder(order.copy(orderStatus = newStatus))
    }

    suspend fun deleteOrder(order: OrderEntity) {
        orderDao.deleteOrder(order)
    }
}
