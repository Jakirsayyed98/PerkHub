package app.perkhub.room.Home

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.perkhub.Screens.Home.Model.Home.HomeRes

@Entity(tableName = "home_cache")
data class HomeCache(
    @PrimaryKey val id: Int = 1,   // Always keep a single row
    val homeResponse: HomeRes,     // Renamed for clarity
    val lastUpdated: Long = System.currentTimeMillis()
)
