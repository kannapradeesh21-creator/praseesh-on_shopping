package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Categories : Screen("categories", "Categories")
    object Deals : Screen("deals", "Today's Deals")
    object Cart : Screen("cart", "Cart")
    object Profile : Screen("profile", "Account")
    object ProductDetail : Screen("product_detail", "Product Details")
    object Checkout : Screen("checkout", "Checkout")
    object OrderSuccess : Screen("order_success", "Order Confirmed")
    object Admin : Screen("admin", "Admin Panel")
    object AiAssistant : Screen("ai_assistant", "AI Shopping Guide")
}
