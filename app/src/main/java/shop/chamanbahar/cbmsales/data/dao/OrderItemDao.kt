package shop.chamanbahar.cbmsales.data.dao

import androidx.room.*
import shop.chamanbahar.cbmsales.data.entities.OrderItem

@Dao
interface OrderItemDao  {

    // ✅ Pure insert — will fail if conflict (keeps old data)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOrderItem(orderItem: OrderItem): Long

    // Insert or update (REPLACE on conflict)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(orderItem: OrderItem)

    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)

    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Int): List<OrderItem>

    @Query("DELETE FROM order_items WHERE id = :orderItemId")
    suspend fun deleteOrderItemById(orderItemId: Int)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Int): List<OrderItem>
}
