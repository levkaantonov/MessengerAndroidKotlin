package levkaantonov.com.study.telegaclone.ui.screens.register

import androidx.fragment.app.Fragment
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.*
import levkaantonov.com.study.telegaclone.utils.*

class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        register_input_code.addTextChangedListener(AppTextWatcher {
            val string: String = register_input_code.text.toString()
            if (string.length == 6)
                enterCode()
        })
    }

    private fun enterCode() {
        val code = register_input_code.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber

                REF_DB_ROOT
                    .child(NODE_USERS)
                    .child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener{ snapshot ->
                        if(!snapshot.hasChild(CHILD_USERNAME)){
                            dateMap[CHILD_USERNAME] = uid
                        }

                        REF_DB_ROOT
                            .child(NODE_PHONES)
                            .child(phoneNumber)
                            .setValue(uid)
                            .addOnFailureListener { showToast(it.message.toString()) }
                            .addOnSuccessListener {
                                REF_DB_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                    .addOnSuccessListener {
                                        showToast("Добро пожаловать")
                                        restartActivity()
                                    }
                                    .addOnFailureListener { showToast(it.message.toString()) }
                            }
                    })
            } else showToast(task.exception?.message.toString())
        }
    }
}