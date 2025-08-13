package shop.chamanbahar.cbmsales.helper

import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.model.Product

data class CartItem(
    val product: Product,
    var quantity: Int,
    var unit: String,
    var discount: Double = 0.0
) {
    fun calculateTotal(discountOverride: Double = discount): Double {
        val rate = product.rate(discountOverride)
        return rate * quantity
    }

    fun toOrderItem(orderId: Int): OrderItem {
        val rate = product.rate(discount)
        val subtotal = rate * quantity

        return OrderItem(
            id = 0, // Let Room autogenerate the primary key
            orderId = orderId,
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            unit = unit,
            rate = rate,
            discount = discount,
            subtotal = subtotal,
            imageResId = product.imageResId // 👈 Link image
        )
    }
}
