package shop.chamanbahar.cbmsales.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val retailerId: Int,            // ✅ FK to Retailer
    val date: Long,
    val discount: Double,
    val totalAmount: Double,
    val notes: String? = "",
    val status: String = "Pending",
    val isCompleted: Boolean = false, // ✅ NEW field for pending/completed
    val isCancelled: Boolean = false
)
