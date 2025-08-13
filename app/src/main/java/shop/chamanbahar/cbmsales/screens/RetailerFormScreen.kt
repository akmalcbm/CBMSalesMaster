package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.viewmodel.OrderViewModel
import shop.chamanbahar.cbmsales.data.entities.Retailer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailerFormScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔥 Form fields
    var retailerName by remember { mutableStateOf("") }
    var retailerPhone by remember { mutableStateOf("") }
    var retailerAddress by remember { mutableStateOf("") }

    // ✅ For edit mode
    var isEditing by remember { mutableStateOf(false) }
    var editingRetailerId by remember { mutableStateOf<Int?>(null) }

    // ✅ Confirmation dialog state
    var retailerToDelete by remember { mutableStateOf<Retailer?>(null) }

    // ✅ Live retailer list from DB
    val retailers by orderViewModel.retailers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Retailers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // --- FORM (Same as before) ---
            OutlinedTextField(
                value = retailerName,
                onValueChange = { retailerName = it },
                label = { Text("Retailer Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = retailerPhone,
                onValueChange = { retailerPhone = it },
                label = { Text("Phone Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = retailerAddress,
                onValueChange = { retailerAddress = it },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (retailerName.isNotBlank() && retailerPhone.isNotBlank() && retailerAddress.isNotBlank()) {
                        scope.launch {
                            if (isEditing && editingRetailerId != null) {
                                // UPDATE
                                orderViewModel.updateRetailer(
                                    Retailer(
                                        id = editingRetailerId!!,
                                        name = retailerName,
                                        phone = retailerPhone,
                                        address = retailerAddress
                                    )
                                )
                                snackbarHostState.showSnackbar("✅ Retailer updated!")
                                isEditing = false
                                editingRetailerId = null
                            } else {
                                // ADD
                                orderViewModel.addRetailer(retailerName, retailerPhone, retailerAddress)
                                snackbarHostState.showSnackbar("✅ Retailer added!")
                            }
                            retailerName = ""
                            retailerPhone = ""
                            retailerAddress = ""
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("⚠️ Please fill all fields")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isEditing) "Update Retailer" else "Save Retailer")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Saved Retailers", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                items(retailers) { retailer ->
                    RetailerListItem(
                        retailer = retailer,
                        onEdit = {
                            retailerName = it.name
                            retailerPhone = it.phone
                            retailerAddress = it.address
                            editingRetailerId = it.id
                            isEditing = true
                        },
                        onDelete = {
                            retailerToDelete = it   // ✅ SHOW DIALOG INSTEAD OF INSTANT DELETE
                        }
                    )
                }
            }
        }

        // 🔴 CONFIRM DELETE DIALOG
        if (retailerToDelete != null) {
            AlertDialog(
                onDismissRequest = { retailerToDelete = null },
                title = { Text("Delete Retailer") },
                text = { Text("Are you sure you want to delete '${retailerToDelete!!.name}'? This cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            orderViewModel.deleteRetailer(retailerToDelete!!)
                            snackbarHostState.showSnackbar("🗑 Retailer deleted")
                            retailerToDelete = null
                        }
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { retailerToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


@Composable
fun RetailerListItem(
    retailer: Retailer,
    onEdit: (Retailer) -> Unit,
    onDelete: (Retailer) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(retailer.name, style = MaterialTheme.typography.titleMedium)
                Text("📱 ${retailer.phone}", style = MaterialTheme.typography.bodyMedium)
                Text("🏠 ${retailer.address}", style = MaterialTheme.typography.bodySmall)
            }

            Row {
                IconButton(onClick = { onEdit(retailer) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(retailer) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
