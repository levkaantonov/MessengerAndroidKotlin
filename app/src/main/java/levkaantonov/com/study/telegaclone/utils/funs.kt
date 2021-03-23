package levkaantonov.com.study.telegaclone.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import levkaantonov.com.study.telegaclone.MainActivity
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.AUTH
import levkaantonov.com.study.telegaclone.database.updatePhonesToDb
import levkaantonov.com.study.telegaclone.models.CommonModel
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_LONG).show()
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}


fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
        return
    }

    APP_ACTIVITY.supportFragmentManager
        .beginTransaction()
        .replace(R.id.data_container, fragment)
        .commit()

}

fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}


fun ImageView.downloadAndSetImage(url: String) {
    Picasso
        .get()
        .load(url)
        .fit()
        .placeholder(R.drawable.default_photo)
        .into(this)
}

fun initContacts() {
    if (AUTH.currentUser == null) {
        return
    }

    if (checkPermission(READ_CONTACTS)) {
        val arrayOfContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            while (cursor.moveToNext()) {
                val fullName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                arrayOfContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesToDb(arrayOfContacts)
    }
}


fun String.asTime(): String {
    val time = Date(this.toLong())
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(time)
}

fun getFilenameFromUri(uri: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null )
    try{
        if(cursor != null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }catch (e: Exception){
        showToast(e.message.toString())
    }finally {
        cursor?.close()
        return result
    }
}

fun getPlurals(count: Int) = APP_ACTIVITY.resources.getQuantityString(R.plurals.count_members, count, count)



