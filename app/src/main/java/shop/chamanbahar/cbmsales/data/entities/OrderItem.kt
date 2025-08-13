package shop.chamanbahar.cbmsales.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ✅ Primary Key (auto-generated)
    val orderId: Int,
    val productName: String, // ✅ You probably store product name here
    val productId: Int,
    val rate: Double,
    val quantity: Int,
    val unit: String,
    val discount: Double,
    val subtotal: Double,
    val imageResId: Int // 👈 New column for drawable resource
)

