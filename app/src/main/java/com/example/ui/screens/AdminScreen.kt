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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.SampleData
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SaleRed
import com.example.ui.theme.StockGreen
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: ShopViewModel
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val isGeneratingDesc by viewModel.isGeneratingAiDesc.collectAsState()
    val generatedDesc by viewModel.generatedDescription.collectAsState()
    val isGeneratingBanner by viewModel.isGeneratingBanner.collectAsState()
    val generatedBanner by viewModel.generatedMarketingBanner.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Overview, 1: Products, 2: Orders, 3: AI Marketing

    // State for Add / Edit Product Dialog
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    var formTitle by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("Electronics") }
    var formPrice by remember { mutableStateOf("") }
    var formOriginalPrice by remember { mutableStateOf("") }
    var formWeightSize by remember { mutableStateOf("") }
    var formStock by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formImageUrl by remember { mutableStateOf("") }
    var formIsDeal by remember { mutableStateOf(false) }

    // When AI generates description, populate form
    LaunchedEffect(generatedDesc) {
        if (generatedDesc.isNotBlank()) {
            formDescription = generatedDesc
            viewModel.clearGeneratedDescription()
        }
    }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = AccentGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Owner Admin Control Panel",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.logoutAdmin()
                        viewModel.navigateBack()
                    }) {
                        Text("Logout", color = AccentGold, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryNavy)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AccentGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Products (${allProducts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Orders (${allOrders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = { Text("AI Tools", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    // OVERVIEW TAB
                    val totalRevenue = allOrders.sumOf { it.totalPrice }
                    val lowStockCount = allProducts.count { it.stockQuantity <= 5 }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Business Performance Metrics",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard(
                                    title = "Total Revenue",
                                    value = "Rs. ${totalRevenue.toInt()}",
                                    icon = Icons.Default.LocalAtm,
                                    color = StockGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Total Orders",
                                    value = "${allOrders.size}",
                                    icon = Icons.Default.Receipt,
                                    color = PrimaryNavy,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard(
                                    title = "Active Products",
                                    value = "${allProducts.size}",
                                    icon = Icons.Default.Inventory,
                                    color = Color(0xFF2563EB),
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Low Stock Alert",
                                    value = "$lowStockCount items",
                                    icon = Icons.Default.Warning,
                                    color = if (lowStockCount > 0) SaleRed else StockGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Store Operations Hotline",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "All WhatsApp orders and customer inquiries route automatically to 0759786878.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // PRODUCTS TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Catalog Inventory",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = {
                                        editingProduct = null
                                        formTitle = ""
                                        formCategory = "Electronics"
                                        formPrice = ""
                                        formOriginalPrice = ""
                                        formWeightSize = ""
                                        formStock = "20"
                                        formDescription = ""
                                        formImageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80"
                                        formIsDeal = false
                                        showProductDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("add_product_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Product", fontSize = 12.sp)
                                }
                            }
                        }

                        items(allProducts) { product ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE2E8F0))
                                    ) {
                                        AsyncImage(
                                            model = product.imageUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                        Text(text = "${product.category} • ${product.weightOrSize}", fontSize = 11.sp, color = Color.Gray)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Rs. ${product.price.toInt()}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = if (product.stockQuantity <= 5) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Stock: ${product.stockQuantity}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (product.stockQuantity <= 5) SaleRed else StockGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = {
                                            editingProduct = product
                                            formTitle = product.title
                                            formCategory = product.category
                                            formPrice = product.price.toInt().toString()
                                            formOriginalPrice = product.originalPrice.toInt().toString()
                                            formWeightSize = product.weightOrSize
                                            formStock = product.stockQuantity.toString()
                                            formDescription = product.description
                                            formImageUrl = product.imageUrl
                                            formIsDeal = product.isDeal
                                            showProductDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB))
                                        }

                                        IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SaleRed)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }

                2 -> {
                    // ORDERS TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Order Management (${allOrders.size})",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (allOrders.isEmpty()) {
                            item {
                                Text(
                                    text = "No customer orders placed yet.",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        items(allOrders) { order ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(
                                            text = "Rs. ${order.totalPrice.toInt()}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Text(text = "Customer: ${order.customerName} (📞 ${order.customerPhone})", fontSize = 12.sp)
                                    Text(text = "Address: ${order.deliveryAddress}, ${order.city}", fontSize = 12.sp, color = Color.Gray)
                                    Text(text = "Payment: ${order.paymentMethod}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        Text(text = order.itemsSummary, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                    }

                                    // Status update buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Status: ${order.orderStatus}", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            if (order.orderStatus != "Delivered") {
                                                OutlinedButton(
                                                    onClick = {
                                                        val next = when (order.orderStatus) {
                                                            "Pending" -> "Processing"
                                                            "Processing" -> "Shipped"
                                                            else -> "Delivered"
                                                        }
                                                        viewModel.updateOrderStatus(order, next)
                                                    },
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Mark Next", fontSize = 11.sp)
                                                }
                                            }

                                            IconButton(onClick = { viewModel.deleteOrder(order) }, modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SaleRed, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }

                3 -> {
                    // AI MARKETING TOOLS TAB
                    var campaignTheme by remember { mutableStateOf("Flash Weekend Sale") }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "AI Marketing Banner & Promo Copywriter",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generate compelling slogans, WhatsApp broadcast campaigns, and promotion banners using Gemini AI.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = campaignTheme,
                                        onValueChange = { campaignTheme = it },
                                        label = { Text("Campaign Theme / Sale Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = { viewModel.generateMarketingBanner(campaignTheme) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isGeneratingBanner) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Generating with AI...")
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentGold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Generate AI Promotional Campaign")
                                        }
                                    }

                                    if (generatedBanner.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = "✨ Generated Campaign Copy:",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF92400E)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(text = generatedBanner, fontSize = 13.sp, color = Color(0xFF78350F))
                                            }
                                        }
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
        }
    }

    // Add / Edit Product Dialog
    if (showProductDialog) {
        AlertDialog(
            onDismissRequest = { showProductDialog = false },
            title = {
                Text(
                    text = if (editingProduct == null) "Add New Product" else "Edit Product",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = formTitle,
                            onValueChange = { formTitle = it },
                            label = { Text("Product Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formCategory,
                            onValueChange = { formCategory = it },
                            label = { Text("Category (e.g. Electronics, Fashion)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formPrice,
                                onValueChange = { formPrice = it },
                                label = { Text("Sale Price (Rs.)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = formOriginalPrice,
                                onValueChange = { formOriginalPrice = it },
                                label = { Text("Original Price") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formWeightSize,
                                onValueChange = { formWeightSize = it },
                                label = { Text("Weight / Size / Variant") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = formStock,
                                onValueChange = { formStock = it },
                                label = { Text("Stock Quantity") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // AI Description Generator Button inside Form!
                    item {
                        Button(
                            onClick = {
                                viewModel.generateAiDescription(
                                    title = formTitle.ifBlank { "New Product" },
                                    category = formCategory,
                                    details = formWeightSize
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isGeneratingDesc) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generating Copy with AI...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("✨ Auto-Generate Description with AI", fontSize = 12.sp)
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = formDescription,
                            onValueChange = { formDescription = it },
                            label = { Text("Description") },
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formImageUrl,
                            onValueChange = { formImageUrl = it },
                            label = { Text("Image URL") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = formPrice.toDoubleOrNull() ?: 1000.0
                        val op = formOriginalPrice.toDoubleOrNull() ?: p
                        val discount = if (op > p) (((op - p) / op) * 100).toInt() else 0
                        val stock = formStock.toIntOrNull() ?: 10

                        val newProduct = ProductEntity(
                            id = editingProduct?.id ?: 0,
                            title = formTitle.ifBlank { "Untitled Product" },
                            category = formCategory.ifBlank { "General" },
                            price = p,
                            originalPrice = op,
                            discountPercent = discount,
                            weightOrSize = formWeightSize.ifBlank { "Standard" },
                            stockQuantity = stock,
                            description = formDescription.ifBlank { "High quality item from Pradeesh Online Shopping." },
                            imageUrl = formImageUrl.ifBlank { "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80" },
                            isDeal = formIsDeal
                        )

                        if (editingProduct == null) {
                            viewModel.addProduct(newProduct)
                        } else {
                            viewModel.updateProduct(newProduct)
                        }
                        showProductDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Save Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
