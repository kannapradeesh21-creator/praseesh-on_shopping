package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.ShopBottomNavBar
import com.example.ui.components.ShopTopAppBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SaleRed
import com.example.ui.theme.StockGreen
import com.example.ui.theme.WhatsAppBrandGreen
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val savings by viewModel.cartSavings.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val total by viewModel.cartFinalTotal.collectAsState()

    Scaffold(
        topBar = {
            ShopTopAppBar(
                cartCount = cartCount,
                onCartClick = { /* Already on cart */ },
                onAiAssistantClick = { viewModel.navigateTo(Screen.AiAssistant) },
                onWhatsAppClick = {
                    viewModel.openWhatsApp(
                        context,
                        message = "Hello! I am viewing my cart on Pradeesh Online Shopping and have a question."
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
        if (cartItems.isEmpty()) {
            // Empty Cart State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(45.dp))
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = PrimaryNavy,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Cart is Empty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Explore our top deals, trending electronics, fashion and Ceylon groceries now!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("empty_cart_shop_now_button")
                    ) {
                        Text("Start Shopping", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Active Cart Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("cart_screen_content")
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Shopping Cart ($cartCount items)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "Clear All",
                            fontSize = 12.sp,
                            color = SaleRed,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { viewModel.clearCart() }
                                .padding(4.dp)
                        )
                    }
                }

                items(cartItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("cart_item_card_${item.cartItem.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Product Image
                            Box(
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                AsyncImage(
                                    model = item.product.imageUrl,
                                    contentDescription = item.product.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = item.cartItem.selectedOption.ifBlank { item.product.weightOrSize },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Rs. ${(item.product.price * item.cartItem.quantity).toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Quantity Controls & Delete
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeCartItem(item.cartItem.id) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = SaleRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.updateCartQuantity(item.cartItem.id, item.cartItem.quantity - 1)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                                    }

                                    Text(
                                        text = "${item.cartItem.quantity}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            viewModel.updateCartQuantity(item.cartItem.id, item.cartItem.quantity + 1)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Price Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Order Summary",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subtotal", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Rs. ${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }

                            if (savings > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Discount Savings", fontSize = 13.sp, color = StockGreen)
                                    Text(text = "- Rs. ${savings.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StockGreen)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Island-wide Delivery", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = if (deliveryFee == 0.0) "FREE" else "Rs. ${deliveryFee.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (deliveryFee == 0.0) StockGreen else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (deliveryFee > 0) {
                                Text(
                                    text = "💡 Tip: Add Rs. ${(5000 - subtotal).toInt()} more to get FREE delivery!",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD97706)
                                )
                            }

                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Grand Total", fontSize = 17.sp, fontWeight = FontWeight.Black)
                                Text(
                                    text = "Rs. ${total.toInt()}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // WhatsApp Direct Order from Cart
                        Button(
                            onClick = {
                                val summary = cartItems.joinToString("\n") {
                                    "• ${it.product.title} (x${it.cartItem.quantity}) - Rs. ${(it.product.price * it.cartItem.quantity).toInt()}"
                                }
                                val message = """
                                    🛒 *PRADEESH ONLINE SHOPPING - CART ORDER*
                                    -----------------------------------------
                                    📦 *Items in Cart:*
                                    $summary

                                    💰 *Total Amount:* Rs. ${total.toInt()}
                                    📍 Delivery: Island-wide (COD / Bank transfer)

                                    Please confirm this order and advise dispatch timeline!
                                """.trimIndent()
                                viewModel.openWhatsApp(context, message = message)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("cart_whatsapp_order_button")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Order via WhatsApp (0759786878)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Proceed to Checkout Form
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Checkout) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("proceed_to_checkout_button")
                        ) {
                            Text(
                                text = "Proceed to Checkout",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AccentGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
