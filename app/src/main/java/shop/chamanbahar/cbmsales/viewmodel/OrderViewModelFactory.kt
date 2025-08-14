package shop.chamanbahar.cbmsales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import shop.chamanbahar.cbmsales.data.repository.RetailerRepository
import shop.chamanbahar.cbmsales.repository.OrderRepository

class OrderViewModelFactory(
    private val orderRepository: OrderRepository,
    private val retailerRepository: RetailerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(orderRepository, retailerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

