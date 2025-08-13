package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.Retailer
import shop.chamanbahar.cbmsales.helper.CartItem
import shop.chamanbahar.cbmsales.helper.DatePickerField
import shop.chamanbahar.cbmsales.helper.RetailerSelector
import shop.chamanbahar.cbmsales.model.Product
import shop.chamanbahar.cbmsales.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel,
    products: List<Product> = emptyList()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val retailers by orderViewModel.retailers.collectAsState()

    var selectedRetailer by remember { mutableStateOf<Retailer?>(null) }
    var retailerPhone by remember { mutableStateOf("") }
    var retailerAddress by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val cartItems = remember { mutableStateListOf<CartItem>() }

    // ✅ Group products by variantKey (like category)
    val groupedProducts = remember(products) { products.groupBy { it.variantKey } }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔻 Retailer Dropdown
            item {
                RetailerSelector(
                    retailers = retailers,
                    selectedRetailer = selectedRetailer,
                    onSelect = {
                        selectedRetailer = it
                        retailerPhone = it.phone
                        retailerAddress = it.address
                    },
                    navController = navController
                )
            }

            item {
                OutlinedTextField(
                    value = retailerPhone,
                    onValueChange = { retailerPhone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = retailerAddress,
                    onValueChange = { retailerAddress = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                DatePickerField(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            // 🔽 Grouped Product List
            groupedProducts.forEach { (variantKey, productList) ->

                item {
                    Text(
                        text = variantKey,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Products inside the group
                items(productList) { product ->

                    var qty by remember { mutableStateOf(0) }
                    var discount by remember { mutableStateOf("0") }
                    var unitExpanded by remember { mutableStateOf(false) }
                    var unit by remember { mutableStateOf(product.getAvailableUnits().first()) }

                    val discountVal = discount.toDoubleOrNull()?.coerceIn(0.0, 100.0) ?: 0.0
                    val rate = product.rate(discountVal)
                    val subtotal = rate * qty

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(
                                text = "${product.name} (₹${product.mrp})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(x = (-10).dp), // 👈 Moves the row 10dp to the left
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // 🖼️ Image Placeholder
                                Image(
                                    painter = painterResource(id = product.imageResId),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.CenterVertically),
                                    contentScale = ContentScale.Fit
                                )

                                Column(modifier = Modifier.weight(1f)) {

                                    // Quantity + Dropdown for Unit
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Quantity
                                        OutlinedTextField(
                                            value = qty.toString(),
                                            onValueChange = {
                                                qty = it.toIntOrNull()?.coerceAtLeast(0) ?: 0
                                                val existing =
                                                    cartItems.find { it.product.id == product.id && it.unit == unit }
                                                if (existing != null) {
                                                    existing.quantity = qty
                                                } else {
                                                    cartItems.add(
                                                        CartItem(
                                                            product,
                                                            qty,
                                                            unit,
                                                            discountVal
                                                        )
                                                    )
                                                }
                                            },
                                            label = { Text("Qty") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        )

                                        // ⬇️ Inside your Row (Unit Dropdown Section)
                                        Box(modifier = Modifier.weight(1f)) {
                                            OutlinedTextField(
                                                value = unit,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Unit", fontSize = 12.sp) },
                                                textStyle = LocalTextStyle.current.copy(
                                                    fontSize = 12.sp,
                                                    lineHeight = 14.sp
                                                ),
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(56.dp), // Optional: Controls vertical height
                                                trailingIcon = {
                                                    IconButton(onClick = { unitExpanded = true }) {
                                                        Icon(
                                                            Icons.Default.ArrowDropDown,
                                                            contentDescription = "Dropdown"
                                                        )
                                                    }
                                                }
                                            )

                                            DropdownMenu(
                                                expanded = unitExpanded,
                                                onDismissRequest = { unitExpanded = false }
                                            ) {
                                                product.getAvailableUnits().forEach { unitOption ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                unitOption,
                                                                fontSize = 13.sp,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        },
                                                        onClick = {
                                                            unit = unitOption
                                                            unitExpanded = false

                                                            val existing =
                                                                cartItems.find { it.product.id == product.id && it.unit == unitOption }
                                                            if (existing != null) {
                                                                existing.quantity = qty
                                                                existing.discount = discountVal
                                                            } else {
                                                                cartItems.add(
                                                                    CartItem(
                                                                        product,
                                                                        qty,
                                                                        unitOption,
                                                                        discountVal
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Discount + Subtotal
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = discount,
                                            onValueChange = {
                                                discount = it
                                                val d = it.toDoubleOrNull() ?: 0.0
                                                val existing =
                                                    cartItems.find { it.product.id == product.id && it.unit == unit }
                                                if (existing != null) {
                                                    existing.discount = d
                                                } else {
                                                    cartItems.add(CartItem(product, qty, unit, d))
                                                }
                                            },
                                            label = { Text("Discount %") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        )

                                        Text(
                                            text = "Subtotal: ₹${"%.2f".format(subtotal)}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                // ➖ Divider between categories
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // 📝 Notes field
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4
                )
            }

            // 💵 Total
            item {
                val total = cartItems.sumOf { it.calculateTotal(it.discount) }
                Text(
                    text = "Total: ₹${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // 🧱 Spacer to make Save button visible
            item { Spacer(Modifier.height(10.dp)) }

            // ✅ Save Order Button
            item {
                Button(
                    onClick = {
                        if (selectedRetailer == null) {
                            scope.launch { snackbarHostState.showSnackbar("⚠ Please select a retailer!") }
                            return@Button
                        }
                        if (cartItems.isEmpty()) {
                            scope.launch { snackbarHostState.showSnackbar("⚠ Please add products!") }
                            return@Button
                        }

                        isSaving = true
                        scope.launch {
                            try {
                                val total = cartItems.sumOf { it.calculateTotal(it.discount) }

                                val orderId = orderViewModel.addOrder(
                                    Order(
                                        retailerId = selectedRetailer!!.id,
                                        date = selectedDate,
                                        discount = 0.0,
                                        totalAmount = total,
                                        notes = notes,
                                        isCompleted = false // Make sure new order starts as pending
                                    )
                                )

                                cartItems.filter { it.quantity > 0 }.forEach { item ->
                                    orderViewModel.addOrderItem(item.toOrderItem(orderId.toInt()))
                                }

                                // Reset form
                                selectedRetailer = null
                                retailerPhone = ""
                                retailerAddress = ""
                                selectedDate = System.currentTimeMillis()
                                notes = ""
                                cartItems.clear()

                                snackbarHostState.showSnackbar("✅ Order saved successfully!")

                                // Go to Orders List screen instead of summary
                                navController.navigate("ordersList") {
                                    popUpTo("home") { inclusive = false }
                                    launchSingleTop = true
                                }

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("❌ Error: ${e.message}")
                                e.printStackTrace()
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(22.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Text("Saving…")
                    } else {
                        Text("✅ Save Order")
                    }
                }

            }
        }
    }
}

