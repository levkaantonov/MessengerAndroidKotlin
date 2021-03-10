package levkaantonov.com.study.telegaclone.ui.fragments

import android.view.*
import kotlinx.android.synthetic.main.fragment_change_username.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.utils.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {
    lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        settings_input_username.setText(USER.name)
    }

    override fun change() {
        mNewUsername = settings_input_username.text.toString().toLowerCase()
        if(mNewUsername.isEmpty()){
            showToast("Поле пустое")
            return
        }

        REF_DB_ROOT.child(NODE_USERNAMES)
            .addListenerForSingleValueEvent(AppValueEventListener{
                if(it.hasChild(mNewUsername)){
                    showToast("Такой пользователь уже существует")
                    return@AppValueEventListener
                }

                changeUsername()
            })

    }

    private fun changeUsername() {
        REF_DB_ROOT.child(NODE_USERNAMES).child(mNewUsername)
            .setValue(CURRENT_UID)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME)
            .setValue(mNewUsername)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    showToast(getString(R.string.toast_data_updated))
                    deleteOldUsername()
                    return@addOnCompleteListener
                }

                showToast(it.exception?.message.toString())
            }
    }

    private fun deleteOldUsername() {
        REF_DB_ROOT.child(NODE_USERNAMES).child(USER.name).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    showToast(getString(R.string.toast_data_updated))
                    fragmentManager?.popBackStack()
                    USER.name = mNewUsername
                    return@addOnCompleteListener
                }

                showToast(it.exception?.message.toString())
            }
    }
}