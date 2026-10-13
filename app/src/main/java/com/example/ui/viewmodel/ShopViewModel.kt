package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.SampleData
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.model.CartItemWithProduct
import com.example.data.repository.ShopRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ShopRepository
    private val aiService = GeminiAiService()

    val hotlinePhone = "0759786878"

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = ShopRepository(db.productDao(), db.cartDao(), db.orderDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialProducts()
        }
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>(Screen.Home)

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    // Selected Product for Details
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    fun openProductDetail(product: ProductEntity) {
        _selectedProduct.value = product
        navigateTo(Screen.ProductDetail)
    }

    // Selected Category
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // Products State
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deals: StateFlow<List<ProductEntity>> = repository.deals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSellers: StateFlow<List<ProductEntity>> = repository.bestSellers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newArrivals: StateFlow<List<ProductEntity>> = repository.newArrivals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProducts: StateFlow<List<ProductEntity>> =
        combine(allProducts, _selectedCategory, _searchQuery) { products, category, query ->
            products.filter { product ->
                val matchesCategory = (category == "All" || product.category.equals(category, ignoreCase = true))
                val matchesQuery = query.isBlank() ||
                    product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Items
    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItemsWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.product.price * it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartOriginalTotal: StateFlow<Double> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.product.originalPrice * it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartSavings: StateFlow<Double> = combine(cartOriginalTotal, cartSubtotal) { original, current ->
        (original - current).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val deliveryFee: StateFlow<Double> = cartSubtotal.combine(cartSubtotal) { subtotal, _ ->
        if (subtotal == 0.0 || subtotal >= 5000.0) 0.0 else 350.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartFinalTotal: StateFlow<Double> = combine(cartSubtotal, deliveryFee) { sub, fee ->
        sub + fee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(product: ProductEntity, sizeOrOption: String = "", quantity: Int = 1) {
        viewModelScope.launch {
            val option = if (sizeOrOption.isNotBlank()) sizeOrOption else product.weightOrSize
            repository.addToCart(product.id, option, quantity)
        }
    }

    fun buyNow(product: ProductEntity, sizeOrOption: String = "") {
        viewModelScope.launch {
            addToCart(product, sizeOrOption, 1)
            navigateTo(Screen.Checkout)
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, newQuantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Checkout & Orders
    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun placeOrder(
        name: String,
        phone: String,
        address: String,
        city: String,
        paymentMethod: String,
        notes: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val currentItems = cartItems.value
            val summary = currentItems.joinToString("\n") {
                "• ${it.product.title} (x${it.cartItem.quantity}) - Rs. ${(it.product.price * it.cartItem.quantity).toInt()}"
            }
            val total = cartFinalTotal.value
            val randomSuffix = (1000..9999).random()
            val orderNo = "POS-$randomSuffix"

            val order = OrderEntity(
                orderNumber = orderNo,
                customerName = name,
                customerPhone = phone,
                deliveryAddress = if (notes.isNotBlank()) "$address (Note: $notes)" else address,
                city = city,
                paymentMethod = paymentMethod,
                orderStatus = "Pending",
                totalPrice = total,
                itemsSummary = summary,
                timestamp = System.currentTimeMillis()
            )

            val orderId = repository.placeOrder(order)
            val finalOrder = order.copy(id = orderId)
            _lastPlacedOrder.value = finalOrder
            onSuccess(finalOrder)
            navigateTo(Screen.OrderSuccess)
        }
    }

    // WhatsApp Order generation
    fun buildWhatsAppMessage(order: OrderEntity): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(order.timestamp))
        return """
            🛒 *PRADEESH ONLINE SHOPPING - NEW ORDER* 🛒
            -----------------------------------------
            📋 *Order Number:* #${order.orderNumber}
            📅 *Date:* $formattedDate
            👤 *Customer Name:* ${order.customerName}
            📞 *Contact Number:* ${order.customerPhone}
            📍 *Delivery Address:* ${order.deliveryAddress}, ${order.city}
            💳 *Payment Method:* ${order.paymentMethod}

            📦 *Ordered Items:*
            ${order.itemsSummary}

            💰 *Total Amount:* Rs. ${order.totalPrice.toInt()}
            -----------------------------------------
            Please confirm my order and send tracking details. Thank you!
        """.trimIndent()
    }

    fun openWhatsApp(context: Context, phoneNumber: String = hotlinePhone, message: String) {
        try {
            val clean = phoneNumber.replace(Regex("[^0-9]"), "")
            val international = if (clean.startsWith("0")) "94" + clean.substring(1) else clean
            val url = "https://api.whatsapp.com/send?phone=$international&text=" + URLEncoder.encode(message, "UTF-8")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/94759786878?text=" + URLEncoder.encode(message, "UTF-8"))
                )
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(browserIntent)
            } catch (_: Exception) {}
        }
    }

    fun callHotline(context: Context, phoneNumber: String = hotlinePhone) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    // AI Chat Assistant
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "👋 Welcome to PRADEESH ONLINE SHOPPING! I am your AI Shopping Assistant. How can I assist you with products, flash deals, island-wide delivery, or placing orders via WhatsApp?",
                isFromUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(text = text, isFromUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        _isAiThinking.value = true
        viewModelScope.launch {
            val reply = aiService.generateShoppingResponse(text, allProducts.value)
            val botMsg = ChatMessage(text = reply, isFromUser = false)
            _chatMessages.value = _chatMessages.value + botMsg
            _isAiThinking.value = false
        }
    }

    // Admin Dashboard State & Controls
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    fun verifyAdminPin(pin: String): Boolean {
        if (pin == "1234" || pin == "0759") {
            _isAdminAuthenticated.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateOrderStatus(order: OrderEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(order, newStatus)
        }
    }

    fun deleteOrder(order: OrderEntity) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }

    // AI Tools for Admin
    private val _generatedDescription = MutableStateFlow("")
    val generatedDescription: StateFlow<String> = _generatedDescription.asStateFlow()

    private val _isGeneratingAiDesc = MutableStateFlow(false)
    val isGeneratingAiDesc: StateFlow<Boolean> = _isGeneratingAiDesc.asStateFlow()

    fun generateAiDescription(title: String, category: String, details: String) {
        _isGeneratingAiDesc.value = true
        viewModelScope.launch {
            val desc = aiService.generateProductDescription(title, category, details)
            _generatedDescription.value = desc
            _isGeneratingAiDesc.value = false
        }
    }

    fun clearGeneratedDescription() {
        _generatedDescription.value = ""
    }

    private val _generatedMarketingBanner = MutableStateFlow("")
    val generatedMarketingBanner: StateFlow<String> = _generatedMarketingBanner.asStateFlow()

    private val _isGeneratingBanner = MutableStateFlow(false)
    val isGeneratingBanner: StateFlow<Boolean> = _isGeneratingBanner.asStateFlow()

    fun generateMarketingBanner(theme: String) {
        _isGeneratingBanner.value = true
        viewModelScope.launch {
            val bannerIdea = aiService.generateMarketingBannerIdea(theme)
            _generatedMarketingBanner.value = bannerIdea
            _isGeneratingBanner.value = false
        }
    }
}
