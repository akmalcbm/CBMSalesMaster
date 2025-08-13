package shop.chamanbahar.cbmsales.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retailers")
data class Retailer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String
)
