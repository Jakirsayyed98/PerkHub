#include <jni.h>
#include <string>

// Base URL
extern "C" {

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_MyApi_00024Companion_BaseURL(JNIEnv *env, jobject) {
    std::string baseUrl = "https://blessed-pretty-mammal.ngrok-free.app/api/";
//    std::string baseUrl = "https://api.perkhub.in/api/";
    return env->NewStringUTF(baseUrl.c_str());
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_sendOTP(JNIEnv *env, jobject) {
    return env->NewStringUTF("sendOTP");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_verifyOTP(JNIEnv *env, jobject) {
    return env->NewStringUTF("verifyOTP");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_authorization(JNIEnv *env, jobject) {
    return env->NewStringUTF("authorization");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getUserDetail(JNIEnv *env, jobject) {
    return env->NewStringUTF("getUserDetail");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_saveDetail(JNIEnv *env, jobject) {
    return env->NewStringUTF("savedetail");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getHomePage(JNIEnv *env, jobject) {
    return env->NewStringUTF("getHomePage");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getStoresByCategory(JNIEnv *env, jobject) {
    return env->NewStringUTF("getStoresByCategory");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_genrateSubID(JNIEnv *env, jobject) {
    return env->NewStringUTF("genrateSubID");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getAllLootoffers(JNIEnv *env, jobject) {
    return env->NewStringUTF("getAllLootoffers");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_searchMiniApps(JNIEnv *env, jobject) {
    return env->NewStringUTF("searchMiniApps");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_updateNotificationToken(JNIEnv *env, jobject) {
    return env->NewStringUTF("updateNotificationToken");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getAllGames(JNIEnv *env, jobject) {
    return env->NewStringUTF("getAllGames");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getPopularGames(JNIEnv *env, jobject) {
    return env->NewStringUTF("get-popular-games");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getAllGamesCategory(JNIEnv *env, jobject) {
    return env->NewStringUTF("games-categories");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getTxnList(JNIEnv *env, jobject) {
    return env->NewStringUTF("getTxnList");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_withdrawalsRequest(JNIEnv *env, jobject) {
    return env->NewStringUTF("withdrawal/request");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_withdrawalTransactions(JNIEnv *env, jobject) {
    return env->NewStringUTF("withdrawal/transactions");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_withdrawalGetPaymentMethod(JNIEnv *env, jobject) {
    return env->NewStringUTF("withdrawal/getPaymentMethod");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_withdrawalAddPaymentMethod(JNIEnv *env, jobject) {
    return env->NewStringUTF("withdrawal/addPaymentMethod");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_withdrawalRequest(JNIEnv *env, jobject) {
    return env->NewStringUTF("withdrawal/request");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getNotificationHistory(JNIEnv *env, jobject) {
    return env->NewStringUTF("get-notification-history");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_ticketsByUser(JNIEnv *env, jobject) {
    return env->NewStringUTF("tickets/by_user");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_ticketsCreate(JNIEnv *env, jobject) {
    return env->NewStringUTF("tickets/create");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_ticketsById(JNIEnv *env, jobject) {
    return env->NewStringUTF("tickets");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_ticketsSendMsg(JNIEnv *env, jobject) {
    return env->NewStringUTF("tickets/send-msg");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_offersSearchByStore(JNIEnv *env, jobject) {
    return env->NewStringUTF("offers/search-by-store");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_offersHomepage(JNIEnv *env, jobject) {
    return env->NewStringUTF("offers/homepage");
}

JNIEXPORT jstring JNICALL
Java_app_perkhub_api_NativeEndpoints_getAllStores(JNIEnv *env, jobject) {
    return env->NewStringUTF("allMiniApps");
}

}