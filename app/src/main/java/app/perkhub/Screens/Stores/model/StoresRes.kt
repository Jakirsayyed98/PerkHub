package app.perkhub.Screens.Stores.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.perkhub.Screens.Home.Model.Home.MiniApp

data class StoresRes(
    val data: List<MiniApp>,
    val message: String,
    val status: Int
)
