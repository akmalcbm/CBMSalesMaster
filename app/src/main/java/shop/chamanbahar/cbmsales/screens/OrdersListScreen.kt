package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.R
import shop.chamanbahar.cbmsales.data.entities.OrderWithItems
import shop.chamanbahar.cbmsales.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OrdersListScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val pendingOrders by orderViewModel.pendingOrders.collectAsState()
    val completedOrders by orderViewModel.completedOrders.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val tabs = listOf(
        stringResource(R.string.pending_orders),
        stringResource(R.string.completed_orders)
    )

    val filteredPendingOrders = remember(pendingOrders, searchQuery) {
        pendingOrders.filter { orderWithItems ->
            orderWithItems.order.id.toString().contains(searchQuery, ignoreCase = true) ||
                    orderWithItems.retailer.name.contains(searchQuery, ignoreCase = true)
        }.sortedByDescending { it.order.date }
    }

    val filteredCompletedOrders = remember(completedOrders, searchQuery) {
        completedOrders.filter { orderWithItems ->
            orderWithItems.order.id.toString().contains(searchQuery, ignoreCase = true) ||
                    orderWithItems.retailer.name.contains(searchQuery, ignoreCase = true)
        }.sortedByDescending { it.order.date }
    }

    val currentOrders = when (selectedTabIndex) {
        0 -> filteredPendingOrders
        1 -> filteredCompletedOrders
        else -> emptyList()
    }

    fun refreshData() {
        scope.launch {
            refreshing = true
            orderViewModel.refreshOrders()
            refreshing = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search_orders)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) },
                            icon = {
                                Icon(
                                    imageVector = if (index == 0) Icons.Default.HourglassEmpty else Icons.Default.Check,
                                    contentDescription = title
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(refreshing),
            onRefresh = { refreshData() },
            modifier = Modifier.padding(padding)
        ) {
            if (currentOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            stringResource(R.string.no_matching_orders)
                        } else {
                            stringResource(
                                if (selectedTabIndex == 0) R.string.no_pending_orders
                                else R.string.no_completed_orders
                            )
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentOrders, key = { it.order.id }) { orderWithItems ->
                        OrderListItem(
                            orderWithItems = orderWithItems,
                            dateFormatter = dateFormatter,
                            onClick = {
                                navController.navigate("orderDetail/${orderWithItems.order.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(
    orderWithItems: OrderWithItems,
    dateFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    val order = orderWithItems.order
    val retailer = orderWithItems.retailer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🧾 Order #${order.id}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "🏬 ${retailer.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                StatusChip(isCompleted = order.isCompleted)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📅 ${dateFormatter.format(Date(order.date))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "💰 ₹${"%.2f".format(order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = "📦 ${orderWithItems.items.size} items",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun StatusChip(isCompleted: Boolean) {
    Surface(
        color = if (isCompleted) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = if (isCompleted) stringResource(R.string.completed)
            else stringResource(R.string.pending),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
