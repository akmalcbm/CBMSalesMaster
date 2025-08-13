package shop.chamanbahar.cbmsales.data

import androidx.room.Database
import androidx.room.RoomDatabase
import shop.chamanbahar.cbmsales.data.dao.OrderDao
import shop.chamanbahar.cbmsales.data.dao.OrderItemDao
import shop.chamanbahar.cbmsales.data.dao.RetailerDao
import shop.chamanbahar.cbmsales.data.entities.Order
import shop.chamanbahar.cbmsales.data.entities.OrderItem
import shop.chamanbahar.cbmsales.data.entities.Retailer

@Database(
    entities = [Retailer::class, Order::class, OrderItem::class],
    version = 2, // ⬅️ bump this whenever schema changes
    exportSchema = false
)
abstract class CBMDatabase : RoomDatabase() {
    abstract fun retailerDao(): RetailerDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
}
