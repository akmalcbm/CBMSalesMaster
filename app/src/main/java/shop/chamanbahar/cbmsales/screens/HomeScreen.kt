package shop.chamanbahar.cbmsales.screens

// Compose foundations and layout
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*

// Compose runtime
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// UI resources
import androidx.compose.ui.res.stringResource
import shop.chamanbahar.cbmsales.R

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

// ViewModels
import shop.chamanbahar.cbmsales.viewmodel.ProductViewModel
import shop.chamanbahar.cbmsales.viewmodel.SettingsViewModel

// Model
import shop.chamanbahar.cbmsales.model.Product

// Coroutine Scope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    viewModel: ProductViewModel
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isGrid by viewModel.isGrid.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // 🛠 Drawer Title (Optional Header)
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                // ⚙ Settings Option
                Text(
                    text = stringResource(R.string.settings),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { drawerState.close() }
                            navController.navigate("settings")
                        }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                // 🆕 ✅ Add Retailer Option
                Text(
                    text = "\uD83E\uDDD1\uD83C\uDFFB\u200D\uD83D\uDCBC Retailers",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { drawerState.close() }
                            navController.navigate("retailerForm")
                        }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                // 📊 ➡ Order List Summary Show Details after the click of List Items
                Text(
                    text = "📊 All Orders",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { drawerState.close() }
                            navController.navigate("ordersList")   // ✅ Make sure this route exists in AppNavGraph
                        }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)


                // 📦 Future: More drawer items can go here...

            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Open drawer")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleLayout() }) {
                            Icon(
                                imageVector = if (isGrid) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                                contentDescription = "Toggle layout"
                            )
                        }
                        // 🆕 ADD ORDER ICON HERE
                        IconButton(onClick = {
                            navController.navigate("orderScreen")
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Go to Orders")
                        }
                    }
                )
            },

            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(text = stringResource(R.string.search_hint)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    AnimatedContent(
                        targetState = isGrid,
                        label = "layoutSwitcher",
                        modifier = Modifier.fillMaxSize()
                    ) { grid ->
                        if (grid) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(150.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(products) { product ->
                                    ProductCard(product) {
                                        navController.navigate("details/${product.id}")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                                items(products) { product ->
                                    ProductCard(product) {
                                        navController.navigate("details/${product.id}")
                                    }
                                }
                            }
                        }
                    }
                }
            })
    }
}


@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        }
    }
}

