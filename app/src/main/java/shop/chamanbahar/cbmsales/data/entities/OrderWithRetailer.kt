package shop.chamanbahar.cbmsales.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithRetailer(
    @Embedded val order: Order,

    @Relation(
        parentColumn = "retailerId",
        entityColumn = "id"
    )
    val retailer: Retailer
)
