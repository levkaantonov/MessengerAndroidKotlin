package levkaantonov.com.study.telegaclone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.databinding.ActivityMainBinding
import levkaantonov.com.study.telegaclone.ui.fragments.ChatsFragment
import levkaantonov.com.study.telegaclone.ui.objects.AppDrawer
import levkaantonov.com.study.telegaclone.utils.replaceActivity
import levkaantonov.com.study.telegaclone.utils.replaceFragment

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()
        initFields()
        initFunctionality()
    }

    private fun initFunctionality() {
        if(true){
            replaceActivity(RegisterActivity::class.java)
            return
        }
        setSupportActionBar(mToolbar)
        mAppDrawer.create()
        replaceFragment(ChatsFragment())
    }


    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)
    }
}