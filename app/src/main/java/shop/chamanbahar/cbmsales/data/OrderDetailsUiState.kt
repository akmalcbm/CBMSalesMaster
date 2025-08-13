package shop.chamanbahar.cbmsales.data

import shop.chamanbahar.cbmsales.data.entities.OrderWithItems

data class OrderDetailsUiState(
    val isLoading: Boolean = true,
    val orderWithItems: OrderWithItems? = null
)
