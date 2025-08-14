package shop.chamanbahar.cbmsales.repository

import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.dao.OrderDao
import shop.chamanbahar.cbmsales.data.dao.OrderItemDao
import shop.chamanbahar.cbmsales.data.dao.RetailerDao
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.data.entities.OrderWithItems
import shop.chamanbahar.cbmsales.data.entities.OrderWithRetailer
import shop.chamanbahar.cbmsales.data.entities.Retailer


class OrderRepository(
    private val orderDao: OrderDao,
    private val retailerDao: RetailerDao,
    private val orderItemDao: OrderItemDao
) {

    // --- RETAILERS ---
    fun getRetailersFlow(): Flow<List<Retailer>> = retailerDao.getAllRetailersFlow()

    suspend fun addRetailer(retailer: Retailer): Long = retailerDao.insertRetailer(retailer)
    suspend fun updateRetailer(retailer: Retailer) = retailerDao.updateRetailer(retailer)
    suspend fun deleteRetailer(retailer: Retailer) = retailerDao.deleteRetailer(retailer)

    // --- ORDERS ---
    suspend fun addOrder(order: Order): Long = orderDao.insertOrder(order)
    suspend fun getOrderById(orderId: Int): Order? = orderDao.getOrderById(orderId)
    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)

    // ✅ New flows
    fun getPendingOrdersFlow(): Flow<List<OrderWithItems>> =
        orderDao.getOrdersByCompletion(false)

    fun getCompletedOrdersFlow(): Flow<List<OrderWithItems>> =
        orderDao.getOrdersByCompletion(true)

    suspend fun updateOrderCompletion(orderId: Int, completed: Boolean) {
        orderDao.updateOrderCompletion(orderId, completed)
    }

    fun getCancelledOrdersFlow(): Flow<List<OrderWithItems>> =
        orderDao.getCancelledOrders()

    suspend fun updateOrderCancelled(orderId: Int, cancelled: Boolean) =
        orderDao.updateOrderCancelled(orderId, cancelled)


    suspend fun updateOrderStatus(orderId: Int, status: String) {
        orderDao.updateOrderStatus(orderId, status)
    }



    // --- ORDER ITEMS ---
    suspend fun addOrderItem(item: OrderItem) = orderItemDao.insertOrderItem(item)

    fun getOrdersWithRetailersFlow(): Flow<List<OrderWithRetailer>> =
        orderDao.getOrdersWithRetailersFlow()

    fun getOrderWithItems(orderId: Int): Flow<OrderWithItems?> {
        return orderDao.getOrderWithItems(orderId)
    }

    // Update whole order
    suspend fun updateOrder(order: Order) =
        orderDao.updateOrder(order)

    // Update an order item
    suspend fun updateOrderItem(item: OrderItem) =
        orderDao.updateOrderItem(item)

    suspend fun getOrderWithItemsOnce(orderId: Int): OrderWithItems? {
        return orderDao.getOrderWithItemsOnce(orderId)
    }

    suspend fun deleteOrderItem(orderItemId: Int) {
        orderDao.deleteOrderItem(orderItemId)
    }



}
