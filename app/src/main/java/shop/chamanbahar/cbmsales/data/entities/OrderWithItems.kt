package shop.chamanbahar.cbmsales.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithItems(
    @Embedded val order: Order,

    @Relation(
        parentColumn = "retailerId",
        entityColumn = "id"
    )
    val retailer: Retailer,

    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItem>
)
