package shop.chamanbahar.cbmsales.data.dao

import androidx.room.*
import shop.chamanbahar.cbmsales.data.entities.OrderItem

@Dao
interface OrderItemDao {
    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem): Long

    // Update
    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)

    // Delete by object
    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)

    // Delete by ID
    @Query("DELETE FROM order_items WHERE id = :orderItemId")
    suspend fun deleteOrderItemById(orderItemId: Int)

    // Get all items for an order
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Int): List<OrderItem>
}

