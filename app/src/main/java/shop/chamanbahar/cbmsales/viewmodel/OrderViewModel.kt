package shop.chamanbahar.cbmsales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.data.OrderDetailsUiState
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.data.entities.OrderWithItems
import shop.chamanbahar.cbmsales.data.entities.Retailer
import shop.chamanbahar.cbmsales.repository.OrderRepository

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    // ---------------- Retailers ----------------
    val retailers: StateFlow<List<Retailer>> =
        repository.getRetailersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRetailer(name: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.addRetailer(
                Retailer(name = name, phone = phone, address = address)
            )
        }
    }

    fun updateRetailer(retailer: Retailer) {
        viewModelScope.launch { repository.updateRetailer(retailer) }
    }

    fun deleteRetailer(retailer: Retailer) {
        viewModelScope.launch { repository.deleteRetailer(retailer) }
    }

    // ---------------- Orders ----------------
    suspend fun addOrder(order: Order): Long = repository.addOrder(order)

    fun deleteOrder(order: Order) {
        viewModelScope.launch { repository.deleteOrder(order) }
    }

    // ---------------- Order Items ----------------
    fun addOrderItem(item: OrderItem) {
        viewModelScope.launch { repository.addOrderItem(item) }
    }

    fun updateOrderItem(item: OrderItem) {
        viewModelScope.launch {
            val updatedItem = item.copy(subtotal = item.quantity * item.rate)
            repository.updateOrderItem(updatedItem)

            // After updating the item, recalc the whole order's total
            val orderWithItems = repository.getOrderWithItemsOnce(item.orderId)
            orderWithItems?.let {
                val subtotal = it.items.sumOf { it.subtotal }
                val total = subtotal - (it.order.discount * subtotal / 100)
                repository.updateOrder(it.order.copy(totalAmount = total))
            }
        }
    }


    fun updateOrderStatus(orderId: Int, isCompleted: Boolean) {
        viewModelScope.launch { repository.updateOrderCompletion(orderId, isCompleted) }
    }

    fun updateOrderNotes(orderId: Int, notes: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId) ?: return@launch
            repository.updateOrder(order.copy(notes = notes))
        }
    }

    // ---------------- Reactive lists ----------------
    val pendingOrders: StateFlow<List<OrderWithItems>> =
        repository.getPendingOrdersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedOrders: StateFlow<List<OrderWithItems>> =
        repository.getCompletedOrdersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getOrderWithItems(orderId: Int): StateFlow<OrderWithItems?> =
        repository.getOrderWithItems(orderId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allOrders: StateFlow<List<Order>> =
        repository.getOrdersWithRetailersFlow()
            .map { list -> list.map { it.order } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshOrders() {
        viewModelScope.launch {
            // No-op for Room flows; kept for future extensibility
        }
    }

    // ---------------- Order Details UI State (cached per orderId) ----------------
    private val orderDetailsCache =
        mutableMapOf<Int, StateFlow<OrderDetailsUiState>>()

    fun getOrderDetailsUiState(orderId: Int): StateFlow<OrderDetailsUiState> =
        orderDetailsCache.getOrPut(orderId) {
            repository.getOrderWithItems(orderId)
                .mapLatest { order ->
                    if (order == null) {
                        OrderDetailsUiState(isLoading = true, orderWithItems = null)
                    } else {
                        OrderDetailsUiState(isLoading = false, orderWithItems = order)
                    }
                }
                // Avoid emitting identical states (prevents tiny flashes on DB micro-updates)
                .distinctUntilChanged()
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    OrderDetailsUiState(isLoading = true)
                )
        }

    fun removeOrderItem(orderItemId: Int) {
        viewModelScope.launch {
            repository.deleteOrderItem(orderItemId)
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status) // ✅ match property name
        }
    }

    val cancelledOrders: StateFlow<List<OrderWithItems>> =
        repository.getCancelledOrdersFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "Cancelled")
        }
    }




}
