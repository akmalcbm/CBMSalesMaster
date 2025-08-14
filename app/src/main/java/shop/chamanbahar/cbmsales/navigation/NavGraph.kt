package shop.chamanbahar.cbmsales.navigation

import ProductDetailsScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import shop.chamanbahar.cbmsales.data.DatabaseProvider
import shop.chamanbahar.cbmsales.data.repository.RetailerRepository
import shop.chamanbahar.cbmsales.repository.OrderRepository
import shop.chamanbahar.cbmsales.screens.*
import shop.chamanbahar.cbmsales.viewmodel.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavGraph(
    settingsViewModel: SettingsViewModel,
    productViewModel: ProductViewModel = viewModel()
) {
    val navController = rememberNavController()

    // 🧠 Get context and create DB instance
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDatabase(context) }

    // 🧾 OrderViewModel via factory
    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(
            OrderRepository(db.orderDao(), db.orderItemDao()),
            RetailerRepository(db.retailerDao())
        )
    )

    // 🚦 Wait for settings to load before deciding start destination
    val languageSelectedState by settingsViewModel.languageSelected.collectAsState()

    // This prevents showing wrong screen before DataStore loads
    val isLoaded by remember { derivedStateOf { languageSelectedState != null } }

    if (!isLoaded) {
        // Splash or loading state
        SplashScreen()
    } else {
        val startDest = if (languageSelectedState) "home" else "language"

        NavHost(navController = navController, startDestination = startDest) {

            // 🌐 Language Selector
            composable("language") {
                LanguageSelectorScreen(
                    onLanguageSelected = {
                        navController.navigate("home") {
                            popUpTo("language") { inclusive = true }
                        }
                    },
                    viewModel = settingsViewModel
                )
            }

            // 🏠 Home Screen
            composable("home") {
                HomeScreen(navController, settingsViewModel, productViewModel)
            }

            // 🛍️ Product Details
            composable("details/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                val product = productViewModel.products.value.find { it.id == productId }

                product?.let {
                    ProductDetailsScreen(
                        initialProduct = it,
                        discountPercentInit = productViewModel.discountPercent.value,
                        allProducts = productViewModel.products.value
                    )
                }
            }

            // ⚙️ Settings
            composable("settings") {
                SettingsScreen(settingsViewModel)
            }

            // 📝 Add New Retailer
            composable("retailerForm") {
                RetailerFormScreen(
                    navController = navController,
                    orderViewModel = orderViewModel
                )
            }

            // 🛒 Place New Order
            composable("orderScreen") {
                OrderScreen(
                    navController = navController,
                    orderViewModel = orderViewModel,
                    products = productViewModel.products.value
                )
            }

            // 📋 Orders List
            composable("ordersList") {
                OrdersListScreen(
                    navController = navController,
                    orderViewModel = orderViewModel
                )
            }

            // 📦 Order Details
            composable(
                route = "orderDetail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                OrderDetailsScreen(
                    navController = navController,
                    orderId = orderId,
                    orderViewModel = orderViewModel
                )
            }
        }
    }
}

@Composable
fun SplashScreen() {
    // Simple splash while loading prefs
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

