package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val total by viewModel.cartFinalTotal.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var fullName by remember { mutableStateOf("Pradeesh") }
    var phoneNumber by remember { mutableStateOf("0759786878") }
    var address by remember { mutableStateOf("No. 45, Temple Road") }
    var city by remember { mutableStateOf("Colombo") }
    var orderNotes by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("Cash on Delivery") } // or "Bank Payment"
    var errorMessage by remember { mutableStateOf("") }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout & Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("checkout_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryNavy)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // WhatsApp Fast Order
                    Button(
                        onClick = {
                            if (fullName.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
                                errorMessage = "Please fill in your name, phone and delivery address"
                                return@Button
                            }
                            val summary = cartItems.joinToString("\n") {
                                "• ${it.product.title} (x${it.cartItem.quantity}) - Rs. ${(it.product.price * it.cartItem.quantity).toInt()}"
                            }
                            val message = """
                                🛒 *PRADEESH ONLINE SHOPPING - DIRECT CHECKOUT* 🛒
                                👤 Customer: $fullName
                                📞 Contact: $phoneNumber
                                📍 Address: $address, $city
                                💳 Payment: $selectedPayment
                                ${if (orderNotes.isNotBlank()) "📝 Notes: $orderNotes\n" else ""}
                                📦 Items:
                                $summary

                                💰 Total: Rs. ${total.toInt()}
                                -----------------------------------------
                                Please confirm my order!
                            """.trimIndent()

                            // Also record the order locally
                            viewModel.placeOrder(
                                name = fullName,
                                phone = phoneNumber,
                                address = address,
                                city = city,
                                paymentMethod = selectedPayment,
                                notes = orderNotes
                            ) {
                                viewModel.openWhatsApp(context, message = message)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("checkout_whatsapp_button")
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

                    // Standard Place Order Button
                    Button(
                        onClick = {
                            if (fullName.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
                                errorMessage = "Please fill in your name, phone and delivery address"
                                return@Button
                            }
                            viewModel.placeOrder(
                                name = fullName,
                                phone = phoneNumber,
                                address = address,
                                city = city,
                                paymentMethod = selectedPayment,
                                notes = orderNotes
                            ) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_order_button")
                    ) {
                        Text(
                            text = "Confirm Order (Rs. ${total.toInt()})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage.isNotBlank()) {
                item {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            // Customer Contact & Delivery Details
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delivery Information",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input")
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Contact Phone / WhatsApp") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input")
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Street Address / House No.") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_input")
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City / Town / District") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_city_input")
                        )

                        OutlinedTextField(
                            value = orderNotes,
                            onValueChange = { orderNotes = it },
                            label = { Text("Order Notes (optional)") },
                            placeholder = { Text("e.g. Call before delivery, Leave at gate") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Payment Option Selection
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select Payment Method",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Option 1: Cash on Delivery
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (selectedPayment == "Cash on Delivery") PrimaryNavy else Color(0xFFE2E8F0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPayment = "Cash on Delivery" }
                                .padding(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedPayment == "Cash on Delivery",
                                onClick = { selectedPayment = "Cash on Delivery" },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryNavy)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.LocalAtm, contentDescription = null, tint = StockGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Cash on Delivery (COD)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Pay in cash when package arrives at your door", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Option 2: Bank Payment / Transfer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (selectedPayment == "Bank Payment") PrimaryNavy else Color(0xFFE2E8F0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPayment = "Bank Payment" }
                                .padding(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedPayment == "Bank Payment",
                                onClick = { selectedPayment = "Bank Payment" },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryNavy)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Direct Bank Payment / Deposit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Transfer to our official shop account & send slip", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Bank Details Card when Bank Payment selected
                        if (selectedPayment == "Bank Payment") {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "🏦 PRADEESH SHOP OFFICIAL BANK ACCOUNT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(text = "Bank: Commercial Bank of Ceylon", fontSize = 12.sp, color = Color(0xFF78350F))
                                    Text(text = "Account Name: PRADEESH ONLINE SHOPPING", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF78350F))
                                    Text(text = "Account Number: 100084729183", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF78350F))
                                    Text(text = "Branch: Colombo Main City Branch", fontSize = 11.sp, color = Color(0xFF78350F))
                                    Text(
                                        text = "Please send receipt slip to WhatsApp 0759786878 after payment.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Summary Breakdown
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
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Order Summary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Items Subtotal", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Rs. ${subtotal.toInt()}", fontSize = 13.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(if (deliveryFee == 0.0) "FREE" else "Rs. ${deliveryFee.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Payable", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Rs. ${total.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
