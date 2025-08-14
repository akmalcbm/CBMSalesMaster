package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.data.entities.Retailer
import shop.chamanbahar.cbmsales.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navController: NavHostController,
    orderId: Int,
    orderViewModel: OrderViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val uiStateFlow = remember(orderId) { orderViewModel.getOrderDetailsUiState(orderId) }
    val uiState by uiStateFlow.collectAsStateWithLifecycle()

    var isEditingNotes by rememberSaveable { mutableStateOf(false) }
    var editedNotes by rememberSaveable { mutableStateOf("") }
    var itemToRemove by remember { mutableStateOf<OrderItem?>(null) }
    var showDeleteOrderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.orderWithItems?.order?.notes) {
        editedNotes = uiState.orderWithItems?.order?.notes.orEmpty()
    }

    fun updateOrderStatus(isCompleted: Boolean) {
        scope.launch {
            orderViewModel.updateOrderStatus(orderId, isCompleted)
            snackbarHostState.showSnackbar(
                if (isCompleted) "Order marked as completed"
                else "Order reverted to pending"
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    uiState.orderWithItems?.order?.let { order ->
                        IconButton(onClick = { updateOrderStatus(!order.isCompleted) }) {
                            Icon(
                                imageVector = if (order.isCompleted) Icons.Default.Check
                                else Icons.Default.HourglassEmpty,
                                contentDescription = "Toggle Status"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.orderWithItems != null -> {
                val orderWithItems = uiState.orderWithItems!!
                val order = orderWithItems.order
                val items = orderWithItems.items
                val retailer = orderWithItems.retailer

                val mrpSubtotal = remember(items) {
                    items.sumOf {
                        val mrpRate = if (it.discount >= 100) 0.0 else it.rate / (1 - (it.discount / 100))
                        mrpRate * it.quantity
                    }
                }
                val totalDiscountAmount = remember(items) {
                    items.sumOf {
                        val mrpRate = it.rate / (1 - (it.discount / 100))
                        (mrpRate * it.quantity) - it.subtotal
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { OrderHeaderCard(order, retailer, dateFormatter, timeFormatter) }

                    item {
                        NotesSection(
                            notes = editedNotes,
                            isEditing = isEditingNotes,
                            onEditToggle = { isEditingNotes = !isEditingNotes },
                            onNotesChange = { editedNotes = it },
                            onSave = {
                                scope.launch {
                                    orderViewModel.updateOrderNotes(orderId, editedNotes)
                                    isEditingNotes = false
                                }
                            },
                            isEditable = !order.isCompleted
                        )
                    }

                    item {
                        Text("Order Items (${items.size})", style = MaterialTheme.typography.titleMedium)
                    }

                    items(items) { product ->
                        OrderProductCard(
                            item = product,
                            onQuantityChange = { newQty ->
                                scope.launch { orderViewModel.updateOrderItem(product.copy(quantity = newQty)) }
                            },
                            onRemove = { selectedItem -> itemToRemove = selectedItem },
                            isEditable = !order.isCompleted
                        )
                    }

                    if (!order.isCompleted) {
                        item {
                            Button(
                                onClick = { navController.navigate("order_screen?editOrderId=${order.id}") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Products")
                                Spacer(Modifier.width(8.dp))
                                Text("Add More Products")
                            }
                        }
                    }

                    item {
                        OrderSummaryCard(
                            subtotal = mrpSubtotal,
                            discountAmount = totalDiscountAmount,
                            total = mrpSubtotal - totalDiscountAmount
                        )
                    }

                    if (!order.isCompleted && order.status != "Cancelled") {
                        item {
                            DangerButton(
                                text = "Cancel Order",
                                onClick = {
                                    scope.launch {
                                        orderViewModel.updateOrderStatus(orderId, status = "Cancelled")
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Delete order button
                    item {
                        DangerButton(
                            text = "Delete Order",
                            onClick = { showDeleteOrderDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Remove item dialog
                if (itemToRemove != null) {
                    AlertDialog(
                        onDismissRequest = { itemToRemove = null },
                        title = { Text("Remove Product") },
                        text = { Text("Are you sure you want to remove '${itemToRemove!!.productName}' from the order?") },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    orderViewModel.removeOrderItem(itemToRemove!!.id)
                                    itemToRemove = null
                                }
                            }) { Text("Remove", color = MaterialTheme.colorScheme.error) }
                        },
                        dismissButton = {
                            TextButton(onClick = { itemToRemove = null }) { Text("Cancel") }
                        }
                    )
                }

                // Delete order confirmation dialog
                if (showDeleteOrderDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteOrderDialog = false },
                        title = { Text("Delete Order") },
                        text = { Text("Are you sure you want to permanently delete this order?") },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    orderViewModel.deleteOrder(order)
                                    navController.popBackStack()
                                }
                            }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteOrderDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text("Order not found", style = MaterialTheme.typography.bodyLarge) }
            }
        }
    }
}



@Composable
fun OrderHeaderCard(
    order: Order,
    retailer: Retailer,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Order #${order.id}",
                    style = MaterialTheme.typography.titleLarge
                )
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
                Column {
                    Text(
                        "Placed on ${dateFormatter.format(Date(order.date))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "At ${timeFormatter.format(Date(order.date))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    "₹${"%.2f".format(order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Retailer: ${retailer.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Phone: ${retailer.phone}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun OrderProductCard(
    item: OrderItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: (OrderItem) -> Unit,
    isEditable: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.productName,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(item.productName, style = MaterialTheme.typography.titleMedium)

                    if (isEditable) {
                        IconButton(onClick = { onRemove(item) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Text("Product ID: ${item.productId}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Quantity: ${item.quantity} ${item.unit}")
                        Text("Rate: ₹${"%.2f".format(item.rate)}")
                        if (item.discount > 0) {
                            Text("Discount: ${item.discount}%")
                        }
                    }
                    Text(
                        "₹${"%.2f".format(item.subtotal)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (isEditable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    QuantitySelector(
                        currentQty = item.quantity,
                        onQtyChange = onQuantityChange
                    )
                }
            }
        }
    }
}


@Composable
fun NotesSection(
    notes: String,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    isEditable: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🧾 Order Notes",
                    style = MaterialTheme.typography.titleMedium
                )

                if (isEditable) {
                    if (isEditing) {
                        Button(onClick = onSave) {
                            Text("Save")
                        }
                    } else {
                        IconButton(onClick = onEditToggle) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit notes")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notes") },
                    maxLines = 4
                )
            } else {
                Text(
                    notes.ifBlank { "No notes added" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun OrderSummaryCard(subtotal: Double, discountAmount: Double, total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🧾 Order Summary", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("💵 Subtotal")
                Text("₹${"%.2f".format(subtotal)}")
            }

            if (discountAmount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🎉 Discounts")
                    Text("-₹${"%.2f".format(discountAmount)}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("💰 Total", style = MaterialTheme.typography.titleMedium)
                Text("₹${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(text)
    }
}

@Composable
fun QuantitySelector(currentQty: Int, onQtyChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { onQtyChange(currentQty - 1) },
            enabled = currentQty > 1
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease")
        }

        Text(
            currentQty.toString(),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(
            onClick = { onQtyChange(currentQty + 1) }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase")
        }
    }
}

