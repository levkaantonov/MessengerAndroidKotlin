package levkaantonov.com.study.telegaclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.databinding.ActivityRegisterBinding
import levkaantonov.com.study.telegaclone.ui.fragments.EnterPhoneNumberFragment
import levkaantonov.com.study.telegaclone.utils.replaceFragment

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mToolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onStart(){
        super.onStart()
        mToolbar = mBinding.registerToolbar
        setSupportActionBar(mToolbar)
        title = getString(R.string.register_title_your_phone)
       replaceFragment(EnterPhoneNumberFragment())
    }
}