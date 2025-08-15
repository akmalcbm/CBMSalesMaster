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

    companion object {
        fun fromOrderItem(orderItem: OrderItem, allProducts: List<Product>): CartItem {
            val product = allProducts.find { it.id == orderItem.productId }
                ?: Product(
                    id = orderItem.productId,
                    name = orderItem.productName,
                    description = "",
                    imageResId = orderItem.imageResId,
                    code = orderItem.rate,
                    mrp = orderItem.rate,
                    weight = 0.0,
                    bundle = 0.0,
                    bori = 0.0,
                    boriScheme = "NA",
                    variantKey = "",
                    category = "",
                    websiteUrl = ""
                )

            return CartItem(
                product = product,
                quantity = orderItem.quantity,
                unit = orderItem.unit,
                discount = orderItem.discount
            )
        }
    }




}
