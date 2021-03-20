package levkaantonov.com.study.telegaclone.database

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.*
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.models.UserModel
import levkaantonov.com.study.telegaclone.utils.APP_ACTIVITY
import levkaantonov.com.study.telegaclone.utils.AppValueEventListener
import levkaantonov.com.study.telegaclone.utils.showToast
import java.io.File


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

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty())
                USER.username = CURRENT_UID

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
        .addListenerForSingleValueEvent(AppValueEventListener {
            if (it.hasChild(newUserName)) {
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
            if (task.isSuccessful) {
                REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
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
    REF_DB_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_updated))
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName
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


fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    fileName: String
) {

    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = fileName

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DB_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}


fun getMessageKey(id: String) = REF_DB_ROOT
    .child(NODE_MESSAGES)
    .child(CURRENT_UID)
    .child(id).push().key.toString()


fun uploadFileToStorage(uri: Uri, messageKey: String, receivedID: String, typeMessage: String, fileName: String = "") {
    val path = REF_STORAGE_ROOT.child(
        FOLDER_FILES
    ).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(
                receivedID,
                it,
                messageKey,
                typeMessage,
                fileName
            )
        }
    }
}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener{ function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}