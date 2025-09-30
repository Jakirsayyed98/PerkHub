package app.perkhub.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.perkhub.Screens.Home.Model.Notification.Data

@Database(
    entities = [HomeCache::class,Data::class,app.perkhub.Screens.Home.Model.TransactionModel.Data::class], // ✅ Correct entity name
    version = 1,
    exportSchema = false
)
@TypeConverters(HomeResponseConverter::class) // ✅ Required for HomeRes (custom type)
abstract class HomeDatabase : RoomDatabase() {

    abstract fun homeCacheDao(): HomeCacheDao
    abstract fun notificationDao(): NotificationDao
    abstract fun storeTransactionDao(): StoreTransactionsDao

    companion object {
        @Volatile
        private var INSTANCE: HomeDatabase? = null

        fun getInstance(context: Context): HomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeDatabase::class.java,
                    "home_database"
                )
                    .fallbackToDestructiveMigration() // ✅ prevents crashes if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
