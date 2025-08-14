package shop.chamanbahar.cbmsales.data.repository

import kotlinx.coroutines.flow.Flow
import shop.chamanbahar.cbmsales.data.dao.RetailerDao
import shop.chamanbahar.cbmsales.data.entities.Retailer

class RetailerRepository(private val retailerDao: RetailerDao) {

    fun getAllRetailers(): Flow<List<Retailer>> =
        retailerDao.getAllRetailers()

    suspend fun insertRetailer(retailer: Retailer) =
        retailerDao.insertRetailer(retailer)

    suspend fun updateRetailer(retailer: Retailer) =
        retailerDao.updateRetailer(retailer)

    suspend fun deleteRetailer(retailer: Retailer) =
        retailerDao.deleteRetailer(retailer)
}
