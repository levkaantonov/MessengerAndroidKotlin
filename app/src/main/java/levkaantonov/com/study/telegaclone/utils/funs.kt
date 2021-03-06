package levkaantonov.com.study.telegaclone.utils

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.ui.fragments.ChatsFragment

fun Fragment.showToast(message: String){
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.replaceActivity(activity: Class<out AppCompatActivity>){
    val intent = Intent(this, activity)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addStack: Boolean = true){
    if (addStack){
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.dataContainer, fragment)
            .commit()
        return
    }

    supportFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .commit()

}

fun Fragment.replaceFragment(fragment: Fragment){
    this.fragmentManager
        ?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(R.id.dataContainer, fragment)
        ?.commit()
}