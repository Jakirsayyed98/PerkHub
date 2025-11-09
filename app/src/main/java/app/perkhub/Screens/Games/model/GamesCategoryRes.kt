package app.perkhub.Screens.Games.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class GamesCategoryRes(
    val data: List<GameCategoryData>,
    val message: String,
    val status: Int
)

@Entity(tableName = "games_category")
data class GameCategoryData(
    @PrimaryKey
    val id: String,
    val name: String,
    val status: Boolean,
    val icon: String
)
