package shop.chamanbahar.cbmsales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Insert
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.data.OrderDetailsUiState
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.data.entities.OrderWithItems
import shop.chamanbahar.cbmsales.data.entities.Retailer
import shop.chamanbahar.cbmsales.data.repository.RetailerRepository
import shop.chamanbahar.cbmsales.repository.OrderRepository

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val retailerRepository: RetailerRepository
) : ViewModel() {

    val pendingOrders: StateFlow<List<OrderWithItems>> =
        orderRepository.getPendingOrdersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val completedOrders: StateFlow<List<OrderWithItems>> =
        orderRepository.getCompletedOrdersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cancelledOrders: StateFlow<List<OrderWithItems>> =
        orderRepository.getCancelledOrdersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val retailers: StateFlow<List<Retailer>> =
        retailerRepository.getAllRetailers()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getOrderDetails(orderId: Int): Flow<OrderWithItems?> =
        orderRepository.getOrderWithItems(orderId)

    fun setOrderCompleted(orderId: Int, completed: Boolean) = viewModelScope.launch {
        orderRepository.updateOrderCompletion(orderId, completed)
        if (completed) orderRepository.updateOrderStatusString(orderId, "Completed")
        else orderRepository.updateOrderStatusString(orderId, "Pending")
    }

    fun cancelOrder(orderId: Int) = viewModelScope.launch {
        orderRepository.setOrderCancelled(orderId, true)
        orderRepository.updateOrderStatusString(orderId, "Cancelled")
    }

    fun updateOrderNotes(orderId: Int, notes: String) = viewModelScope.launch {
        val order = orderRepository.getOrderById(orderId) ?: return@launch
        orderRepository.updateOrder(order.copy(notes = notes))
    }

    fun updateOrderItem(item: OrderItem) = viewModelScope.launch {
        val updatedItem = item.copy(subtotal = item.quantity * item.rate)
        orderRepository.updateOrderItem(updatedItem)

        orderRepository.getOrderWithItemsOnce(item.orderId)?.let { owi ->
            val subtotal = owi.items.sumOf { it.subtotal }
            val total = subtotal - (owi.order.discount * subtotal / 100)
            orderRepository.updateOrder(owi.order.copy(totalAmount = total))
        }
    }

    fun removeOrderItem(orderItemId: Int) = viewModelScope.launch {
        orderRepository.deleteOrderItem(orderItemId)
    }

    fun deleteOrder(order: Order) = viewModelScope.launch {
        orderRepository.deleteOrder(order)
    }

    fun refreshOrders() { /* no-op */ }


    suspend fun addOrder(order: Order): Long {
        return orderRepository.insertOrder(order)
    }

    fun addOrderItem(orderItem: OrderItem) = viewModelScope.launch {
        orderRepository.insertOrderItem(orderItem)
    }

    fun addRetailer(name: String, phone: String, address: String) = viewModelScope.launch {
        val retailer = Retailer(
            id = 0, // auto-generated
            name = name,
            phone = phone,
            address = address
        )
        retailerRepository.insertRetailer(retailer)
    }

    fun updateRetailer(retailer: Retailer) = viewModelScope.launch {
        retailerRepository.updateRetailer(retailer)
    }

    fun deleteRetailer(retailer: Retailer) = viewModelScope.launch {
        retailerRepository.deleteRetailer(retailer)
    }



}


