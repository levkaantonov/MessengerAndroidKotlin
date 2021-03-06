package levkaantonov.com.study.telegaclone.ui.fragments

import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*
import levkaantonov.com.study.telegaclone.MainActivity
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.utils.AUTH
import levkaantonov.com.study.telegaclone.utils.AppTextWatcher
import levkaantonov.com.study.telegaclone.utils.replaceActivity
import levkaantonov.com.study.telegaclone.utils.showToast

class EnterCodeFragment(val phoneNumber: String, val id: String) : BaseFragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).title = phoneNumber
        register_input_code.addTextChangedListener(AppTextWatcher{
                val string: String = register_input_code.text.toString()
                if(string.length == 6)
                    enterCode( )
        })
    }

    private fun enterCode() {
        val code = register_input_code.text.toString()
        val creds = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(creds).addOnCompleteListener {
            if(it.isSuccessful){
                showToast("Добро пожаловать")
                (activity as RegisterActivity).replaceActivity(MainActivity::class.java)
            }else{
                showToast(it.exception?.message.toString())
            }
        }
    }
}