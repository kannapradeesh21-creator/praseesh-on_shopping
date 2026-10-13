package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SampleData
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopBottomNavBar
import com.example.ui.components.ShopTopAppBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CategoriesScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    Scaffold(
        topBar = {
            ShopTopAppBar(
                cartCount = cartCount,
                onCartClick = { viewModel.navigateTo(Screen.Cart) },
                onAiAssistantClick = { viewModel.navigateTo(Screen.AiAssistant) },
                onWhatsAppClick = {
                    viewModel.openWhatsApp(
                        context,
                        message = "Hello! I am browsing the categories at Pradeesh Online Shopping."
                    )
                },
                onCallClick = { viewModel.callHotline(context) }
            )
        },
        bottomBar = {
            ShopBottomNavBar(
                currentScreen = currentScreen,
                cartCount = cartCount,
                onSelectScreen = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("categories_screen_content")
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Product Categories",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Browse curated collections and departments",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Category Horizontal Tabs with Icons
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(SampleData.categories) { category ->
                        val isSelected = category.equals(selectedCategory, ignoreCase = true)
                        val count = if (category == "All") allProducts.size else allProducts.count { it.category == category }
                        val icon = getCategoryIcon(category)

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryNavy else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                            modifier = Modifier
                                .clickable { viewModel.selectCategory(category) }
                                .testTag("category_select_$category")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isSelected) AccentGold else Color(0xFFE2E8F0),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) PrimaryNavy else Color(0xFF475569),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.size(8.dp))

                                Column {
                                    Text(
                                        text = category,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Text(
                                        text = "$count items",
                                        color = if (isSelected) AccentGold else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Products in Selected Category
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Showing $selectedCategory (${products.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            val chunked = products.chunked(2)
            items(chunked) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (product in pair) {
                        ProductCard(
                            product = product,
                            onProductClick = { viewModel.openProductDetail(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            onBuyNow = { viewModel.buyNow(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Electronics" -> Icons.Default.Devices
        "Fashion" -> Icons.Default.LocalMall
        "Groceries & Spices" -> Icons.Default.Eco
        "Beauty & Fragrance" -> Icons.Default.Spa
        "Watches & Accessories" -> Icons.Default.Watch
        "Home & Living" -> Icons.Default.Home
        else -> Icons.Default.Check
    }
}
