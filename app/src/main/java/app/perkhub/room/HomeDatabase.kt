package app.perkhub.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.perkhub.Screens.Games.model.GameCategoryData
import app.perkhub.Screens.Games.model.GamesTypeConverters
import app.perkhub.Screens.Home.Model.Notification.Data
import app.perkhub.Screens.Profile.model.TicketMessage
import app.perkhub.room.Tickets.TicketMessageDao
import app.perkhub.room.Tickets.TicketsDao
import app.perkhub.room.games.GamesCategoryDao
import app.perkhub.room.games.GamesDao

@Database(
    entities = [HomeCache::class,Data::class,app.perkhub.Screens.Home.Model.TransactionModel.Data::class,
        app.perkhub.Screens.Profile.model.Data::class,TicketMessage::class, app.perkhub.Screens.Games.model.Data::class,GameCategoryData::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(HomeResponseConverter::class, GamesTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {

    abstract fun homeCacheDao(): HomeCacheDao
    abstract fun notificationDao(): NotificationDao
    abstract fun storeTransactionDao(): StoreTransactionsDao
    abstract fun ticketsDao(): TicketsDao
    abstract fun ticketMessageDao(): TicketMessageDao
    abstract fun gamesDao():GamesDao
    abstract fun gamesCategoryDao():GamesCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: HomeDatabase? = null

        fun getInstance(context: Context): HomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeDatabase::class.java,
                    "perkhub_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
