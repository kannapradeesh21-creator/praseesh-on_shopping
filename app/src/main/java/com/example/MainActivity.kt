package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.DealsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PradeeshShopApp()
                }
            }
        }
    }
}

@Composable
fun PradeeshShopApp(
    viewModel: ShopViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            Screen.Home -> HomeScreen(viewModel)
            Screen.Categories -> CategoriesScreen(viewModel)
            Screen.Deals -> DealsScreen(viewModel)
            Screen.Cart -> CartScreen(viewModel)
            Screen.Profile -> ProfileScreen(viewModel)
            Screen.ProductDetail -> ProductDetailScreen(viewModel)
            Screen.Checkout -> CheckoutScreen(viewModel)
            Screen.OrderSuccess -> OrderSuccessScreen(viewModel)
            Screen.Admin -> AdminScreen(viewModel)
            Screen.AiAssistant -> AiChatScreen(viewModel)
        }
    }
}
