package shop.chamanbahar.cbmsales.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.entities.*

@Dao
interface OrderDao {

    // --- RETAILER QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailer(retailer: Retailer)

    @Query("SELECT * FROM retailers ORDER BY name")
    suspend fun getAllRetailers(): List<Retailer>

    @Transaction
    @Query("SELECT * FROM retailers ORDER BY name")
    fun getAllRetailersFlow(): Flow<List<Retailer>> // ✅ Now in DAO

    // --- ORDER QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Query("SELECT * FROM orders ORDER BY date DESC")
    suspend fun getAllOrders(): List<Order>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Int): Order?

    @Delete
    suspend fun deleteOrder(order: Order)

    // --- ORDER ITEMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Int): List<OrderItem>

    // --- RELATIONS ---
    @Transaction
    @Query("SELECT * FROM orders ORDER BY date DESC")
    suspend fun getOrdersWithRetailers(): List<OrderWithRetailer>



    // ✅ New: Pending orders
    @Transaction
    @Query("SELECT * FROM orders WHERE isCompleted = 0 ORDER BY date DESC")
    fun getPendingOrdersFlow(): Flow<List<OrderWithItems>>

    // ✅ New: Completed orders
    @Transaction
    @Query("SELECT * FROM orders WHERE isCompleted = 1 ORDER BY date DESC")
    fun getCompletedOrdersFlow(): Flow<List<OrderWithItems>>

    // ✅ New: Full order details
    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderWithItems(orderId: Int): Flow<OrderWithItems?>


    @Query("SELECT * FROM orders WHERE isCompleted = :completed ORDER BY date DESC")
    fun getOrdersByCompletion(completed: Boolean): Flow<List<OrderWithItems>>

    @Query("UPDATE orders SET isCompleted = :completed WHERE id = :orderId")
    suspend fun updateOrderCompletion(orderId: Int, completed: Boolean)

    @Query("UPDATE orders SET isCompleted = :isCompleted WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, isCompleted: Boolean)

    @Query("SELECT * FROM orders WHERE isCancelled = 1 ORDER BY date DESC")
    fun getCancelledOrders(): Flow<List<OrderWithItems>>

    @Query("UPDATE orders SET isCancelled = :cancelled WHERE id = :orderId")
    suspend fun updateOrderCancelled(orderId: Int, cancelled: Boolean)


    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)


    @Transaction
    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getOrdersWithRetailersFlow(): Flow<List<OrderWithRetailer>>

    @Update
    suspend fun updateOrder(order: Order)

    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderWithItemsOnce(orderId: Int): OrderWithItems?

    @Query("DELETE FROM order_items WHERE id = :orderItemId")
    suspend fun deleteOrderItem(orderItemId: Int)



}
