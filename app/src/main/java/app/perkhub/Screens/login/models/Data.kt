package app.perkhub.Screens.login.models

data class Data(
    val user_id: String,
    val name: String,
    val email: String,
    val number: String,
    val gender: String,
    val dob: String,
    val fcm_token: String,
    val verified:Boolean,
    val blocked:Boolean,
    val updated_at: String,
    val status: String,
)
