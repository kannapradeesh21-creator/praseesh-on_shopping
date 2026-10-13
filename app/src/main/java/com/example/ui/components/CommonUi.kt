package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entity.ProductEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SaleRed
import com.example.ui.theme.StockGreen
import com.example.ui.theme.WhatsAppBrandGreen
import kotlinx.coroutines.delay

@Composable
fun ShopTopAppBar(
    cartCount: Int,
    onCartClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Surface(
        color = PrimaryNavy,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Header with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AccentGold, Color(0xFFD97706))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Pradeesh Online Shopping Logo",
                            tint = PrimaryNavy,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "PRADEESH",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ONLINE SHOPPING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }

                // Quick Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // WhatsApp Hotline Button
                    IconButton(
                        onClick = onWhatsAppClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(WhatsAppBrandGreen.copy(alpha = 0.2f), CircleShape)
                            .testTag("whatsapp_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "WhatsApp Hotline 0759786878",
                            tint = WhatsAppBrandGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // AI Shopping Guide Button
                    IconButton(
                        onClick = onAiAssistantClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(AccentGold.copy(alpha = 0.2f), CircleShape)
                            .testTag("ai_assistant_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Shopping Assistant",
                            tint = AccentGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Cart with Badge
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("cart_header_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = SaleRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = cartCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hotline Contact Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .clickable { onCallClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hotline & Orders: 0759786878",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = WhatsAppBrandGreen,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "24/7 Available",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBarWithCategories(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryNavy)
            .padding(bottom = 12.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Search phones, fashion, spices, perfumes...",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AccentGold
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedBorderColor = AccentGold,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("product_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Pills
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = category.equals(selectedCategory, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) AccentGold else Color(0xFF1E293B),
                    modifier = Modifier
                        .clickable { onSelectCategory(category) }
                        .testTag("category_chip_${category.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) PrimaryNavy else Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBannerCarousel(
    onBannerClick: (Int) -> Unit
) {
    val bannerImages: List<Int> = listOf(
        R.drawable.banner_mega_sale_1791802411704,
        R.drawable.banner_gadgets_1791802438924,
        R.drawable.banner_fashion_supermart_1791802460775
    )

    val pagerState = rememberPagerState(pageCount = { bannerImages.size })

    // Auto scroll timer
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % bannerImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) { page ->
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(page) }
                    .testTag("hero_banner_card_$page")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = bannerImages[page],
                        contentDescription = "Promotional Banner $page",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dots Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(bannerImages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 20.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isSelected) AccentGold else Color(0xFFCBD5E1))
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBuyNow: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onProductClick(product) }
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product Image & Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .background(Color(0xFFF1F5F9))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Badge (Top Left)
                if (product.discountPercent > 0) {
                    Surface(
                        color = SaleRed,
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Category Tag (Top Right)
                Surface(
                    color = PrimaryNavy.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(bottomStart = 10.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = product.category,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Deal or Best Seller pill (Bottom Left)
                if (product.isDeal) {
                    Surface(
                        color = AccentGold,
                        shape = RoundedCornerShape(topEnd = 8.dp),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = PrimaryNavy,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "FLASH DEAL",
                                color = PrimaryNavy,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Info Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Title
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Weight / Size
                Text(
                    text = product.weightOrSize,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Rating and Reviews
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.rating} (${product.reviewsCount})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pricing Row
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Rs. ${product.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (product.originalPrice > product.price) {
                        Text(
                            text = "Rs. ${product.originalPrice.toInt()}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Stock availability badge
                val isLowStock = product.stockQuantity in 1..5
                Surface(
                    color = if (isLowStock) Color(0xFFFEF3C7) else Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isLowStock) "Only ${product.stockQuantity} left!" else "In Stock (${product.stockQuantity} units)",
                        color = if (isLowStock) Color(0xFFB45309) else Color(0xFF047857),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons: Add to Cart and Buy Now
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAddToCart(product) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("add_to_cart_button_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onBuyNow(product) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("buy_now_button_${product.id}")
                    ) {
                        Text(text = "Buy Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HotlineOrderingCard(
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("hotline_ordering_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WhatsApp Quick Order",
                    color = AccentGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Send your items directly to 0759786878 for instant dispatch!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onWhatsAppClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("hotline_whatsapp_action")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "WhatsApp", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ShopBottomNavBar(
    currentScreen: Screen,
    cartCount: Int,
    onSelectScreen: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("shop_bottom_nav_bar")
    ) {
        val items = listOf(
            Triple(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
            Triple(Screen.Categories, Icons.Filled.Category, Icons.Outlined.Category),
            Triple(Screen.Deals, Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer),
            Triple(Screen.Cart, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
            Triple(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEach { (screen, selectedIcon, unselectedIcon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(screen) },
                icon = {
                    if (screen == Screen.Cart) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = SaleRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(cartCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = screen.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) selectedIcon else unselectedIcon,
                            contentDescription = screen.title
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryNavy,
                    selectedTextColor = PrimaryNavy,
                    indicatorColor = AccentGold,
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}
