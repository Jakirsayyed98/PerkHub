package app.perkhub.room.Store

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_cache")
data class StoreCacheEntity(
    @PrimaryKey
    val cacheKey: String,
    val updatedAt: Long,
    val expiresAt: Long
)