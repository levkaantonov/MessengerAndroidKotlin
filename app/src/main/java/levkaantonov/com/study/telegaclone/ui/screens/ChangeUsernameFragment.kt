package levkaantonov.com.study.telegaclone.ui.screens

import kotlinx.android.synthetic.main.fragment_change_username.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.USER
import levkaantonov.com.study.telegaclone.database.tryUpdateUserName
import levkaantonov.com.study.telegaclone.utils.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {
    lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        settings_input_username.setText(USER.username)
    }

    override fun change() {
        mNewUsername = settings_input_username.text.toString().toLowerCase()
        if(mNewUsername.isEmpty()){
            showToast("Поле пустое")
            return
        }
        tryUpdateUserName(mNewUsername)
    }
}