package shop.chamanbahar.cbmsales.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.entities.Retailer

@Dao
interface RetailerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailer(retailer: Retailer): Long

    @Query("SELECT * FROM retailers ORDER BY name ASC")
    fun getAllRetailers(): Flow<List<Retailer>>

    @Update
    suspend fun updateRetailer(retailer: Retailer)

    @Delete
    suspend fun deleteRetailer(retailer: Retailer)
}
