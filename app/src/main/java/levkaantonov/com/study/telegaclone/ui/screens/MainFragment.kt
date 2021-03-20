package levkaantonov.com.study.telegaclone.ui.screens

import androidx.fragment.app.Fragment
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.utils.APP_ACTIVITY
import levkaantonov.com.study.telegaclone.utils.hideKeyboard

class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.telegram)
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}