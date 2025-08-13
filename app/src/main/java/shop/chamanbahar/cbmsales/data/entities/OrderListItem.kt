package shop.chamanbahar.cbmsales.data.entities

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OrderListItem(
    order: OrderWithItems,
    onClick: () -> Unit,
    onCompleteClick: () -> Unit,
    isPending: Boolean
) {
    // Access the Order object via order.order
    Text(text = "Order #${order.order.id}")
}
