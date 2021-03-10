package levkaantonov.com.study.telegaclone

import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.databinding.ActivityMainBinding
import levkaantonov.com.study.telegaclone.models.User
import levkaantonov.com.study.telegaclone.ui.fragments.ChatsFragment
import levkaantonov.com.study.telegaclone.ui.objects.AppDrawer
import levkaantonov.com.study.telegaclone.utils.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val mBinding get() = _binding!!
    lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFields()
        initFunctionality()
    }

    private fun initFunctionality() {
        if(AUTH.currentUser == null){
            replaceActivity(RegisterActivity::class.java)
            return
        }
        setSupportActionBar(mToolbar)
        mAppDrawer.create()
        replaceFragment(ChatsFragment(), false)
    }


    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)
        initFirebase()
        initUser()
    }

    private fun initUser() {
        REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID)
            .addListenerForSingleValueEvent(AppValueEventListener{
                USER = it.getValue(User::class.java) ?: User()
            })
    }

}