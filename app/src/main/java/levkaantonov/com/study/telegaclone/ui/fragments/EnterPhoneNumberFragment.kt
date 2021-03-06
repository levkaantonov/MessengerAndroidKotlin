package levkaantonov.com.study.telegaclone.ui.fragments

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import kotlinx.android.synthetic.main.fragment_enter_phone_number.register_input_phone_number
import levkaantonov.com.study.telegaclone.MainActivity
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.utils.AUTH
import levkaantonov.com.study.telegaclone.utils.replaceActivity
import levkaantonov.com.study.telegaclone.utils.replaceFragment
import levkaantonov.com.study.telegaclone.utils.showToast
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class EnterPhoneNumberFragment : BaseFragment(R.layout.fragment_enter_phone_number) {
    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(creds: PhoneAuthCredential) {
                AUTH.signInWithCredential(creds).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Добро пожаловать")
                        (activity as RegisterActivity).replaceActivity(MainActivity::class.java)
                    } else {
                        showToast(it.exception?.message.toString())
                    }
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
                Log.d("FIREBASE EXCEPTION", p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }

        register_btn_next.setOnClickListener {
            sendCode()
        }
    }

    private fun sendCode() {
        if (register_input_phone_number.text.toString().isEmpty())
            showToast(getString(R.string.register_toast_enter_phone))
        else
            authUser()

    }

    private fun authUser() {
        mPhoneNumber = register_input_phone_number.text.toString()
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setActivity(activity as RegisterActivity)
                .setCallbacks(mCallback)
                .setPhoneNumber(mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .build()
        )
    }
}