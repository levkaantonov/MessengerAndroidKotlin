package levkaantonov.com.study.telegaclone

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import levkaantonov.com.study.telegaclone.database.AUTH
import levkaantonov.com.study.telegaclone.database.initFirebase
import levkaantonov.com.study.telegaclone.database.initUser
import levkaantonov.com.study.telegaclone.databinding.ActivityMainBinding
import levkaantonov.com.study.telegaclone.ui.screens.MainFragment
import levkaantonov.com.study.telegaclone.ui.screens.register.EnterPhoneNumberFragment
import levkaantonov.com.study.telegaclone.ui.objects.AppDrawer
import levkaantonov.com.study.telegaclone.utils.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val mBinding get() = _binding!!
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase() {
            initUser() {
                CoroutineScope(Dispatchers.IO).launch {
                    initContacts()
                }
                initFields()
                initFunctionality()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()

        AppStates.updateState(AppStates.OFFLINE)
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    private fun initFunctionality() {
        if(AUTH.currentUser == null){
            replaceFragment(EnterPhoneNumberFragment(), false)
            return
        }
        setSupportActionBar(mToolbar)
        mAppDrawer.create()
        replaceFragment(MainFragment(), false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }
}

