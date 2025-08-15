package shop.chamanbahar.cbmsales.repository

import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.dao.OrderDao
import shop.chamanbahar.cbmsales.data.dao.OrderItemDao
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.data.entities.OrderWithItems

class OrderRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
) {
    // LISTS
    fun getPendingOrdersFlow(): Flow<List<OrderWithItems>> =
        orderDao.getPendingOrdersFlow()

    fun getCompletedOrdersFlow(): Flow<List<OrderWithItems>> =
        orderDao.getCompletedOrdersFlow()

    fun getCancelledOrdersFlow(): Flow<List<OrderWithItems>> =
        orderDao.getCancelledOrdersFlow()

    // DETAILS
    fun getOrderWithItems(orderId: Int): Flow<OrderWithItems?> =
        orderDao.getOrderWithItems(orderId)

    suspend fun getOrderWithItemsOnce(orderId: Int): OrderWithItems? =
        orderDao.getOrderWithItemsOnce(orderId)

    // ORDERS
    suspend fun getOrderById(orderId: Int): Order? = orderDao.getOrderById(orderId)
    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)
    suspend fun updateOrderCompletion(orderId: Int, completed: Boolean) =
        orderDao.updateOrderCompletion(orderId, completed)
    suspend fun setOrderCancelled(orderId: Int, cancelled: Boolean) =
        orderDao.setOrderCancelled(orderId, cancelled)
    suspend fun updateOrderStatusString(orderId: Int, status: String) =
        orderDao.updateOrderStatusString(orderId, status)

    // ITEMS
    suspend fun deleteOrderItem(orderItemId: Int) =
        orderItemDao.deleteOrderItemById(orderItemId)

    suspend fun insertOrder(order: Order) = orderDao.insertOrder(order)

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    suspend fun addOrUpdateOrderItem(orderItem: OrderItem) {
        orderItemDao.insertOrUpdate(orderItem)
    }

    suspend fun insertOrderItem(orderItem: OrderItem) { // ✅ FIX: pure insert
        orderItemDao.insertOrderItem(orderItem)
    }

    suspend fun getOrderItems(orderId: Int) =
        orderItemDao.getItemsForOrder(orderId)

    suspend fun insertOrUpdateOrderItem(orderItem: OrderItem) =
        orderItemDao.insertOrUpdate(orderItem)

    suspend fun updateOrderItem(orderItem: OrderItem) =
        orderItemDao.updateOrderItem(orderItem)

    suspend fun deleteOrderItem(orderItem: OrderItem) =
        orderItemDao.deleteOrderItem(orderItem)
}
