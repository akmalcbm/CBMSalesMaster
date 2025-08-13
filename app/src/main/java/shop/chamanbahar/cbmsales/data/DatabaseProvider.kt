package shop.chamanbahar.cbmsales.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: CBMDatabase? = null

    fun getDatabase(context: Context): CBMDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                CBMDatabase::class.java,
                "cbm_sales_db"
            )
                .fallbackToDestructiveMigration() // ✅ Auto-wipe DB on schema change (dev safe)
                .build()
                .also { INSTANCE = it }
        }
    }
}
