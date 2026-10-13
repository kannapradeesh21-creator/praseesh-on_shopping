package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.example.data.local.entity.ProductEntity
import com.example.ui.components.HeroBannerCarousel
import com.example.ui.components.HotlineOrderingCard
import com.example.ui.components.ProductCard
import com.example.ui.components.SearchBarWithCategories
import com.example.ui.components.ShopBottomNavBar
import com.example.ui.components.ShopTopAppBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SaleRed
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val allProducts by viewModel.filteredProducts.collectAsState()
    val deals by viewModel.deals.collectAsState()
    val bestSellers by viewModel.bestSellers.collectAsState()
    val newArrivals by viewModel.newArrivals.collectAsState()

    Scaffold(
        topBar = {
            Column {
                ShopTopAppBar(
                    cartCount = cartCount,
                    onCartClick = { viewModel.navigateTo(Screen.Cart) },
                    onAiAssistantClick = { viewModel.navigateTo(Screen.AiAssistant) },
                    onWhatsAppClick = {
                        viewModel.openWhatsApp(
                            context = context,
                            message = "Hello Pradeesh Online Shopping! I would like to inquire about your products and place an order."
                        )
                    },
                    onCallClick = { viewModel.callHotline(context) }
                )
                SearchBarWithCategories(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    categories = SampleData.categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { viewModel.selectCategory(it) }
                )
            }
        },
        bottomBar = {
            ShopBottomNavBar(
                currentScreen = currentScreen,
                cartCount = cartCount,
                onSelectScreen = { viewModel.navigateTo(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.AiAssistant) },
                containerColor = AccentGold,
                contentColor = PrimaryNavy,
                modifier = Modifier.testTag("ai_assistant_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Guide",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Guide",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("home_screen_content")
        ) {
            // When user is searching, show direct search results
            if (searchQuery.isNotBlank() || selectedCategory != "All") {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "Search Results for '$searchQuery'" else "$selectedCategory Products",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${allProducts.size} items found",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Grid pairs
                val chunkedProducts = allProducts.chunked(2)
                items(chunkedProducts) { pair ->
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
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // Regular Home Layout

                // 1. Hero Promotional Banners Carousel
                item {
                    HeroBannerCarousel(
                        onBannerClick = { page ->
                            when (page) {
                                0 -> viewModel.navigateTo(Screen.Deals)
                                1 -> viewModel.selectCategory("Electronics")
                                2 -> viewModel.selectCategory("Fashion")
                            }
                        }
                    )
                }

                // 2. WhatsApp Ordering Hotline Highlight Card
                item {
                    HotlineOrderingCard(
                        onWhatsAppClick = {
                            viewModel.openWhatsApp(
                                context,
                                message = "Hello! I am browsing Pradeesh Online Shopping and would like to order."
                            )
                        },
                        onCallClick = { viewModel.callHotline(context) }
                    )
                }

                // 3. Today's Deals Section (Flash Sale with Countdown)
                item {
                    SectionHeader(
                        title = "Today's Flash Deals",
                        subtitle = "Limited time discounts up to 70% off",
                        icon = Icons.Default.FlashOn,
                        badgeText = "ENDS IN 05:24:19",
                        badgeColor = SaleRed,
                        onViewAllClick = { viewModel.navigateTo(Screen.Deals) }
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(deals) { product ->
                            Box(modifier = Modifier.width(190.dp)) {
                                ProductCard(
                                    product = product,
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onBuyNow = { viewModel.buyNow(it) }
                                )
                            }
                        }
                    }
                }

                // 4. Best Sellers Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Best Sellers",
                        subtitle = "Most loved by our customers island-wide",
                        icon = Icons.Default.Star,
                        badgeText = "TOP RATED",
                        badgeColor = AccentGold,
                        onViewAllClick = { viewModel.navigateTo(Screen.Categories) }
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bestSellers) { product ->
                            Box(modifier = Modifier.width(190.dp)) {
                                ProductCard(
                                    product = product,
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onBuyNow = { viewModel.buyNow(it) }
                                )
                            }
                        }
                    }
                }

                // 5. Promotional Mid Banner: "Special Discount Offers"
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    PromoDiscountBanner(
                        onExploreClick = { viewModel.navigateTo(Screen.Deals) }
                    )
                }

                // 6. New Arrivals Section
                item {
                    SectionHeader(
                        title = "New Arrivals",
                        subtitle = "Fresh additions to our catalog",
                        icon = Icons.Default.NewReleases,
                        badgeText = "JUST ARRIVED",
                        badgeColor = Color(0xFF2563EB),
                        onViewAllClick = { viewModel.navigateTo(Screen.Categories) }
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(newArrivals) { product ->
                            Box(modifier = Modifier.width(190.dp)) {
                                ProductCard(
                                    product = product,
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onBuyNow = { viewModel.buyNow(it) }
                                )
                            }
                        }
                    }
                }

                // 7. Full Catalog Grid
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Explore Everything",
                        subtitle = "Genuine products at wholesale & retail prices",
                        icon = Icons.Default.LocalOffer,
                        onViewAllClick = { viewModel.navigateTo(Screen.Categories) }
                    )
                }

                val allChunked = allProducts.chunked(2)
                items(allChunked) { pair ->
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
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String? = null,
    badgeColor: Color = AccentGold,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .clickable { onViewAllClick() }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "See All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "See All",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun PromoDiscountBanner(
    onExploreClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onExploreClick() }
            .testTag("promo_discount_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = SaleRed,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "SPECIAL DISCOUNT OFFERS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Mega Savings on All Brands",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Order via WhatsApp 0759786878 for instant delivery!",
                    color = AccentGold,
                    fontSize = 11.sp
                )
            }

            Surface(
                color = AccentGold,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "UP TO 70% OFF",
                    color = PrimaryNavy,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }
    }
}
