package shop.chamanbahar.cbmsales.navigation

import ProductDetailsScreen
import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import shop.chamanbahar.cbmsales.data.DatabaseProvider
import shop.chamanbahar.cbmsales.data.dao.OrderItemDao
import shop.chamanbahar.cbmsales.data.dao.RetailerDao
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

    // 🚦 Start Destination Logic
    val languageSelected by settingsViewModel.languageSelected.collectAsState()
    val startDest = if (languageSelected) "home" else "language"

    // 🔗 Navigation Graph
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

        // 📋 Orders List (Pending & Completed Tabs)
        composable("ordersList") {
            OrdersListScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }

        // 📦 Single Order Detail Screen
        composable(
            route = "orderDetail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
            OrderDetailsScreen(
                navController = navController,
                orderId = orderId,
                orderViewModel = orderViewModel  // Changed from 'viewModel' to 'orderViewModel'
            )
        }

    }
}
