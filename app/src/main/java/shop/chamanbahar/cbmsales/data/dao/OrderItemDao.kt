package shop.chamanbahar.cbmsales.data.dao

import androidx.room.*
import shop.chamanbahar.cbmsales.data.entities.OrderItem

@Dao
interface OrderItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Int): List<OrderItem>

    @Delete
    suspend fun deleteOrderItem(item: OrderItem)
}
