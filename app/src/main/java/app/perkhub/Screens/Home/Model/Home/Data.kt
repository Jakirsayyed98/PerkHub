package app.perkhub.Screens.Home.Model.Home

data class Data(
    val banner1: ArrayList<Banner1>,
    val banner2: ArrayList<Banner1>,
    val banner3: ArrayList<Any>,
    val categories: ArrayList<Category>,
    val category_list: ArrayList<CategoryX>,
    val popular: ArrayList<MiniApp>,
    val top_cashback: ArrayList<MiniApp>,
    val trending: ArrayList<MiniApp>
)