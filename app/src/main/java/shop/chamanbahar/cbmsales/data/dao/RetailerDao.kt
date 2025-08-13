package shop.chamanbahar.cbmsales.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.entities.Retailer

@Dao
interface RetailerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailer(retailer: Retailer): Long

    @Query("SELECT * FROM retailers ORDER BY name ASC")
    fun getAllRetailersFlow(): Flow<List<Retailer>>  // ✅ Using Flow for auto-updates

    @Update
    suspend fun updateRetailer(retailer: Retailer)

    @Delete
    suspend fun deleteRetailer(retailer: Retailer)
}

