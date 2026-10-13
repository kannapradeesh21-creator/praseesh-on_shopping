package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopBottomNavBar
import com.example.ui.components.ShopTopAppBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SaleRed
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun DealsScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val deals by viewModel.deals.collectAsState()

    Scaffold(
        topBar = {
            ShopTopAppBar(
                cartCount = cartCount,
                onCartClick = { viewModel.navigateTo(Screen.Cart) },
                onAiAssistantClick = { viewModel.navigateTo(Screen.AiAssistant) },
                onWhatsAppClick = {
                    viewModel.openWhatsApp(
                        context,
                        message = "Hello! I am viewing Today's Deals at Pradeesh Online Shopping and want to grab an offer."
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
                .testTag("deals_screen_content")
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "TODAY'S FLASH DEALS",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                            }

                            Surface(
                                color = SaleRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = " Ends in 04:18:22",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Unbeatable Discounts & Clearance Offers",
                            color = AccentGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Limited stock available. Guaranteed island-wide fast doorstep delivery. Call / WhatsApp 0759786878 for instant reservation.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Special Discount Offers (${deals.size} Deals)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            val chunked = deals.chunked(2)
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
