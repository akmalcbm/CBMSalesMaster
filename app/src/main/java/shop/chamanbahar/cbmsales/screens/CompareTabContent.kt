package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import shop.chamanbahar.cbmsales.model.Product

@Composable
fun CompareTabContent(product: Product) {
    Text(
        text = "Coming soon: compare ${product.name} with other brands",
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}
