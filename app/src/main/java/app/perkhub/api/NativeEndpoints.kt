package app.perkhub.api

object NativeEndpoints {

    init {
        System.loadLibrary("perkhub") // Must match your native .so library name
    }

    external fun sendOTP(): String
    external fun verifyOTP(): String
    external fun authorization(): String
    external fun getUserDetail(): String
    external fun saveDetail(): String
    external fun getHomePage(): String
    external fun getStoresByCategory(): String
    external fun genrateSubID(): String
    external fun getAllLootoffers(): String
    external fun searchMiniApps(): String
    external fun updateNotificationToken(): String
    external fun getAllGames(): String
    external fun getAllGamesCategory() :  String
    external fun getPopularGames(): String
    external fun getTxnList(): String
    external fun withdrawalsRequest(): String
    external fun withdrawalTransactions(): String
    external fun withdrawalGetPaymentMethod(): String
    external fun withdrawalAddPaymentMethod(): String
    external fun withdrawalRequest(): String
    external fun getNotificationHistory(): String
    external fun ticketsByUser(): String
    external fun ticketsCreate(): String
    external fun ticketsById(): String
    external fun ticketsSendMsg(): String
    external fun offersSearchByStore(): String
    external fun offersHomepage(): String
}
