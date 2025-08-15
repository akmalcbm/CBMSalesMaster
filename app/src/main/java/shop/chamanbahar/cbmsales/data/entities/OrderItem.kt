package shop.chamanbahar.cbmsales.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    indices = [Index(value = ["orderId", "productId", "unit"], unique = true)]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productName: String,
    val productId: Int,
    val rate: Double,
    val quantity: Int,
    val unit: String,
    val discount: Double,
    val subtotal: Double,
    val imageResId: Int
)


