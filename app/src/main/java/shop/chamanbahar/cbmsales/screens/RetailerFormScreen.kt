package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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
    val retailers by orderViewModel.retailers.collectAsState()

    // Dialog states
    var showDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editingRetailerId by remember { mutableStateOf<Int?>(null) }

    // Form fields (for dialog)
    var retailerName by remember { mutableStateOf("") }
    var retailerPhone by remember { mutableStateOf("") }
    var retailerAddress by remember { mutableStateOf("") }

    // Delete confirmation dialog
    var retailerToDelete by remember { mutableStateOf<Retailer?>(null) }

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    fun openAddDialog() {
        retailerName = ""
        retailerPhone = ""
        retailerAddress = ""
        editingRetailerId = null
        isEditing = false
        showDialog = true
    }

    fun openEditDialog(retailer: Retailer) {
        retailerName = retailer.name
        retailerPhone = retailer.phone
        retailerAddress = retailer.address
        editingRetailerId = retailer.id
        isEditing = true
        showDialog = true
    }

    val filteredRetailers = retailers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery) ||
                it.address.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Retailers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { openAddDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Retailer")
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
                .padding(16.dp)
        ) {
            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Retailers") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredRetailers) { retailer ->
                    RetailerListItem(
                        retailer = retailer,
                        onEdit = { openEditDialog(it) },
                        onDelete = { retailerToDelete = it }
                    )
                }
            }
        }

        // --- Add/Edit Dialog ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (isEditing) "Edit Retailer" else "Add Retailer") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = retailerName,
                            onValueChange = { retailerName = it },
                            label = { Text("Retailer Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = retailerPhone,
                            onValueChange = { retailerPhone = it },
                            label = { Text("Phone Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = retailerAddress,
                            onValueChange = { retailerAddress = it },
                            label = { Text("Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (retailerName.isBlank() || retailerPhone.isBlank() || retailerAddress.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("⚠️ Please fill all fields") }
                        } else {
                            scope.launch {
                                if (isEditing && editingRetailerId != null) {
                                    orderViewModel.updateRetailer(
                                        Retailer(editingRetailerId!!, retailerName, retailerPhone, retailerAddress)
                                    )
                                    snackbarHostState.showSnackbar("✅ Retailer updated!")
                                } else {
                                    orderViewModel.addRetailer(retailerName, retailerPhone, retailerAddress)
                                    snackbarHostState.showSnackbar("✅ Retailer added!")
                                }
                                showDialog = false
                            }
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // --- Delete Confirmation Dialog ---
        if (retailerToDelete != null) {
            AlertDialog(
                onDismissRequest = { retailerToDelete = null },
                title = { Text("Delete Retailer") },
                text = { Text("Are you sure you want to delete '${retailerToDelete!!.name}'?") },
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
        elevation = CardDefaults.cardElevation(2.dp)
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


