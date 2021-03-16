package levkaantonov.com.study.telegaclone.models

data class UserModel(
    val id: String = "",
    var name: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty"
)