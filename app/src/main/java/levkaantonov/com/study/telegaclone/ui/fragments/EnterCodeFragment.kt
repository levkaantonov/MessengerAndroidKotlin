package levkaantonov.com.study.telegaclone.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_enter_code.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.utils.AppTextWatcher
import levkaantonov.com.study.telegaclone.utils.showToast

class EnterCodeFragment : BaseFragment(R.layout.fragment_enter_code) {
    override fun onStart() {
        super.onStart()
        register_input_code.addTextChangedListener(AppTextWatcher{
                val string: String = register_input_code.text.toString()
                if(string.length == 6)
                    verifyCode( )
        })
    }

    private fun verifyCode() {
        showToast("okay")
    }
}