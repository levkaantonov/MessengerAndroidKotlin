package levkaantonov.com.study.telegaclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import levkaantonov.com.study.telegaclone.databinding.ActivityMainBinding
import levkaantonov.com.study.telegaclone.ui.fragments.ChatsFragment
import levkaantonov.com.study.telegaclone.ui.objects.AppDrawer

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
        setSupportActionBar(mToolbar)
        mAppDrawer.create()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.dataContainer, ChatsFragment())
            .commit()
    }


    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)
    }
}