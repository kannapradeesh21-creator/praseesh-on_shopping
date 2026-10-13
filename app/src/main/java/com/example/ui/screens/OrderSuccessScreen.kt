package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.StockGreen
import com.example.ui.theme.WhatsAppBrandGreen
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun OrderSuccessScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val lastOrder by viewModel.lastPlacedOrder.collectAsState()

    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(StockGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Order Placed Successfully!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Thank you for shopping with Pradeesh Online Shopping",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (lastOrder != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Order ID: #${lastOrder!!.orderNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryNavy,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Order Status Tracker
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Delivery Status",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val steps = listOf(
                            Pair("Order Received", "We have verified your order items"),
                            Pair("Processing & Packing", "Preparing package with protective bubble wrap"),
                            Pair("Out for Delivery", "Dispatched with courier to your address"),
                            Pair("Delivered", "Handover & payment collection")
                        )

                        steps.forEachIndexed { index, step ->
                            val isCompleted = index == 0
                            val isCurrent = index == 1
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isCompleted || isCurrent) StockGreen else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        } else {
                                            Text(text = "${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isCurrent) Color.White else Color.Gray)
                                        }
                                    }

                                    if (index < steps.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(28.dp)
                                                .background(if (isCompleted) StockGreen else Color(0xFFE2E8F0))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = step.first,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent || isCompleted) MaterialTheme.colorScheme.onSurface else Color.Gray
                                    )
                                    Text(
                                        text = step.second,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Customer Summary Card
            if (lastOrder != null) {
                val order = lastOrder!!
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Order Details",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            Text("Customer: ${order.customerName} (${order.customerPhone})", fontSize = 13.sp)
                            Text("Address: ${order.deliveryAddress}, ${order.city}", fontSize = 13.sp)
                            Text("Payment: ${order.paymentMethod}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Items:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(order.itemsSummary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Rs. ${order.totalPrice.toInt()}", fontWeight = FontWeight.Black, fontSize = 17.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (lastOrder != null) {
                        Button(
                            onClick = {
                                val msg = viewModel.buildWhatsAppMessage(lastOrder!!)
                                viewModel.openWhatsApp(context, message = msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("success_send_whatsapp_button")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send to WhatsApp (0759786878)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("continue_shopping_button")
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Continue Shopping", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
