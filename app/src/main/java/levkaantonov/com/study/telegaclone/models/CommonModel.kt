package levkaantonov.com.study.telegaclone.models

data class CommonModel(
    val id: String = "",
    var name: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timestamp: Any = ""
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}