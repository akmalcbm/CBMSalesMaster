
package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

@Composable
fun OrdersListScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel
) {
    // ---------- State ----------
    val pendingOrders by orderViewModel.pendingOrders.collectAsState()
    val completedOrders by orderViewModel.completedOrders.collectAsState()
    val cancelledOrders by orderViewModel.cancelledOrders.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val tabs = listOf(
        stringResource(R.string.pending_orders),
        stringResource(R.string.completed_orders),
        stringResource(R.string.cancelled_orders)
    )

    // ---------- Filtering in ONE place ----------
    val filteredOrders = remember(searchQuery, pendingOrders, completedOrders, cancelledOrders) {
        listOf(
            pendingOrders.filter { matchesSearch(it, searchQuery) }.sortedByDescending { it.order.date },
            completedOrders.filter { matchesSearch(it, searchQuery) }.sortedByDescending { it.order.date },
            cancelledOrders.filter { matchesSearch(it, searchQuery) }.sortedByDescending { it.order.date }
        )
    }
    val currentOrders = filteredOrders[selectedTabIndex]

    // ---------- Refresh function ----------
    fun refreshData() {
        scope.launch {
            refreshing = true
            orderViewModel.refreshOrders() // Make sure this loads cancelled too
            refreshing = false
        }
    }

    // ---------- UI ----------
    Scaffold(
        topBar = {
            Column {
                // Search Box
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

                // Tabs
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) },
                            icon = {
                                when (index) {
                                    0 -> Icon(Icons.Default.HourglassEmpty, contentDescription = title)
                                    1 -> Icon(Icons.Default.Check, contentDescription = title)
                                    2 -> Icon(Icons.Default.Close, contentDescription = title)
                                }
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
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val emptyText = when (selectedTabIndex) {
                        0 -> R.string.no_pending_orders
                        1 -> R.string.no_completed_orders
                        2 -> R.string.no_cancelled_orders
                        else -> R.string.no_orders
                    }
                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            stringResource(R.string.no_matching_orders)
                        } else {
                            stringResource(emptyText)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Orders List
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

// ---------- Helpers ----------
private fun matchesSearch(orderWithItems: OrderWithItems, query: String): Boolean {
    return orderWithItems.order.id.toString().contains(query, ignoreCase = true) ||
            orderWithItems.retailer.name.contains(query, ignoreCase = true)
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("🧾 Order #${order.id}", style = MaterialTheme.typography.titleMedium)
                    Text("🏬 ${retailer.name}", style = MaterialTheme.typography.bodyMedium)
                }
                StatusChip(
                    isCompleted = order.isCompleted,
                    isCancelled = order.isCancelled
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("📅 ${dateFormatter.format(Date(order.date))}", style = MaterialTheme.typography.bodyMedium)
                Text("💰 ₹${"%.2f".format(order.totalAmount)}", style = MaterialTheme.typography.titleMedium)
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
fun StatusChip(
    isCompleted: Boolean,
    isCancelled: Boolean
) {
    val (color, label) = when {
        isCancelled -> MaterialTheme.colorScheme.errorContainer to "Cancelled"
        isCompleted -> MaterialTheme.colorScheme.primaryContainer to "Completed"
        else -> MaterialTheme.colorScheme.secondaryContainer to "Pending"
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}