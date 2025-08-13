package shop.chamanbahar.cbmsales.helper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import shop.chamanbahar.cbmsales.model.Product

@Composable
fun ProductOrderRow(
    product: Product,
    defaultUnit: String,
    onAddItem: (qty: Int, unit: String) -> Unit
) {
    var qty by remember { mutableStateOf("0") }
    var selectedUnit by remember { mutableStateOf(defaultUnit) }
    var expanded by remember { mutableStateOf(false) }

    val units = listOf("KG", "Bundle", "Bori", "Pieces")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(product.name, modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = qty,
            onValueChange = {
                qty = it
                onAddItem(it.toIntOrNull() ?: 0, selectedUnit)
            },
            label = { Text("Qty") },
            singleLine = true,
            modifier = Modifier.width(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selectedUnit)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            selectedUnit = unit
                            expanded = false
                            onAddItem(qty.toIntOrNull() ?: 0, selectedUnit)
                        }
                    )
                }
            }
        }
    }
}
