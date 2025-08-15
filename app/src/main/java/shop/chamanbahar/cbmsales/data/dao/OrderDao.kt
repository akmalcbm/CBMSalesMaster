package shop.chamanbahar.cbmsales.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.entities.*

@Dao
interface OrderDao {

    // Order details
    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderWithItems(orderId: Int): Flow<OrderWithItems?>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderWithItemsOnce(orderId: Int): OrderWithItems?

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Int): Order?

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    // Items
    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)

    // Status
    @Query("UPDATE orders SET isCompleted = :completed WHERE id = :orderId")
    suspend fun updateOrderCompletion(orderId: Int, completed: Boolean)

    @Query("""
        UPDATE orders
        SET isCancelled = :cancelled,
            status = CASE WHEN :cancelled = 1 THEN 'Cancelled' ELSE status END
        WHERE id = :orderId
    """)
    suspend fun setOrderCancelled(orderId: Int, cancelled: Boolean)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatusString(orderId: Int, status: String)

    // Lists
    @Transaction
    @Query("SELECT * FROM orders WHERE isCancelled = 0 AND isCompleted = 0 ORDER BY date DESC")
    fun getPendingOrdersFlow(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE isCancelled = 0 AND isCompleted = 1 ORDER BY date DESC")
    fun getCompletedOrdersFlow(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE isCancelled = 1 ORDER BY date DESC")
    fun getCancelledOrdersFlow(): Flow<List<OrderWithItems>>

    // Order lists by completion
    @Transaction
    @Query("SELECT * FROM orders WHERE isCompleted = :completed ORDER BY date DESC")
    fun getOrdersByCompletion(completed: Boolean): Flow<List<OrderWithItems>>

    // Order lists by cancellation
    @Transaction
    @Query("SELECT * FROM orders WHERE isCancelled = :cancelled ORDER BY date DESC")
    fun getOrdersByCancelled(cancelled: Boolean): Flow<List<OrderWithItems>>



    // Update cancelled status
    @Query("UPDATE orders SET isCancelled = :cancelled WHERE id = :orderId")
    suspend fun updateOrderCancelled(orderId: Int, cancelled: Boolean)

    @Insert
    suspend fun insertOrder(order: Order): Long


}

