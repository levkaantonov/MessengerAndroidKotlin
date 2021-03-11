package levkaantonov.com.study.telegaclone.models

data class User(
    val id: String = "",
    var name: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty"
)