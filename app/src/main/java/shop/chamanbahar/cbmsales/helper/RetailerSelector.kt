package shop.chamanbahar.cbmsales.helper

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import shop.chamanbahar.cbmsales.data.entities.Retailer

@Composable
fun RetailerSelector(
    retailers: List<Retailer>,
    selectedRetailer: Retailer?,
    onSelect: (Retailer) -> Unit,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // 🔘 TextField to trigger dropdown
        OutlinedTextField(
            value = selectedRetailer?.name ?: "",
            onValueChange = {},
            label = { Text("Select Retailer") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },  // ✅ Opens menu on click
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Retailer")
                }
            }
        )

        // 📜 ✅ Actual dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // 🔹 Show all retailers
            retailers.forEach { retailer ->
                DropdownMenuItem(
                    text = { Text(retailer.name) },
                    onClick = {
                        println("✅ DEBUG: Retailer clicked -> ${retailer.name}")
                        onSelect(retailer)
                        expanded = false
                    }
                )
            }

            // 🔹 Divider before Add option
            Divider()

            // ➕ Add Retailer Option
            DropdownMenuItem(
                text = { Text("➕ Add New Retailer") },
                onClick = {
                    expanded = false
                    navController.navigate("retailerForm")
                }
            )
        }
    }
}

