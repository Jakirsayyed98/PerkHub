package app.perkhub.Screens.Home.Model.MiniAppByCategory

import app.perkhub.Screens.Home.Model.Home.MiniApp

data class MiniAppByCategoryReq(
    val category_id: String
)

data class MiniAppSearchReq(
    val name: String
)

data class MiniAppbyCategory(
    val data: ArrayList<MiniApp>,
    val message: String,
    val status: Int
)