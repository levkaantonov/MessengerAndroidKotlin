package levkaantonov.com.study.telegaclone.utils

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.models.UserModel

lateinit var AUTH: FirebaseAuth
lateinit var REF_DB_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel
lateinit var CURRENT_UID: String

const val TYPE_TXT = "text"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val NODE_MESSAGES = "messages"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_NAME = "name"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timestamp"


inline fun initFirebase(crossinline function: () -> Unit) {
    AUTH = FirebaseAuth.getInstance()
    REF_DB_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    function()
}

inline fun putUrlToDb(url: String, crossinline function: () -> Unit) {
    REF_DB_ROOT
        .child(NODE_USERS)
        .child(CURRENT_UID)
        .child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.name.isEmpty())
                USER.name = CURRENT_UID

            function()
        })
}


fun updatePhonesToDb(arrayContacts: ArrayList<CommonModel>) {
    REF_DB_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
        it.children.forEach { snapshot ->
            arrayContacts.forEach { contact ->
                if (snapshot.key == USER.phone) {
                    return@forEach
                }

                if (snapshot.key == contact.phone) {
                    REF_DB_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                        .child(snapshot.value.toString()).child(CHILD_ID)
                        .setValue(snapshot.value.toString())
                        .addOnFailureListener { showToast(it.message.toString()) }

                    REF_DB_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                        .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                        .setValue(contact.fullname)
                        .addOnFailureListener { showToast(it.message.toString()) }
                }
            }
        }
    })
}


fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()


fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(msg: String, receivingUserId: String, typeTxt: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"
    val msgKey = REF_DB_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeTxt
    mapMessage[CHILD_TEXT] = msg
    mapMessage[CHILD_ID] = msgKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$msgKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$msgKey"] = mapMessage

    REF_DB_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun tryUpdateUserName(newUserName: String) {
    REF_DB_ROOT.child(NODE_USERNAMES)
        .addListenerForSingleValueEvent(AppValueEventListener{
            if(it.hasChild(newUserName)){
                showToast("Такой пользователь уже существует")
                return@AppValueEventListener
            }
            updateUserName(newUserName)
        })

}

fun updateUserName(newUserName: String) {
    REF_DB_ROOT.child(NODE_USERNAMES).child(newUserName)
        .setValue(CURRENT_UID)
        .addOnCompleteListener { task ->
            if(task.isSuccessful){
                REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME)
                    .setValue(newUserName)
                    .addOnSuccessListener {
                        showToast(APP_ACTIVITY.getString(R.string.toast_data_updated))
                        deleteOldUsername(newUserName)
                    }.addOnFailureListener {
                        showToast(it.message.toString())
                    }
            }
        }
}

fun deleteOldUsername(newUserName: String) {
    REF_DB_ROOT.child(NODE_USERNAMES).child(USER.name).removeValue()
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_updated))
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.name = newUserName
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}


fun updateBioToDb(newBio: String) {
    REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_updated))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun updateFullName(fullName: String) {
    REF_DB_ROOT
        .child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullName)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_updated))
            USER.fullname = fullName
            APP_ACTIVITY.mAppDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}