package app.perkhub.Screens.Games.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class GamesRes(
    val data: List<Data>,
    val message: String,
    val status: Int
)

@Entity(tableName = "games")
data class Data(
    @PrimaryKey
    val id: String,
    val assets: Assets, // <-- Complex type, needs TypeConverter
    val category_id: String,
    val code: String,
    val colorMuted: String,
    val colorVibrant: String,
    val description: String,
    val gamePlays: String,
    val gamePreviews: String,
    val hasIntegratedAds: Boolean,
    val height: String,
    val isPortrait: Boolean,
    val name: String,
    val numberOfRatings: String,
    val popular: Boolean,
    val privateAllowed: Boolean,
    val rating: String,
    val status: Boolean,
    val trending: Boolean,
    val url: String,
    val width: String
)

data class Assets(
    val brick: String,
    val brickTiny: String,
    val cover: String,
    val coverTiny: String,
    val screens: List<String>, // <-- Also complex type
    val square: String,
    val thumb: String,
    val wall: String
)
