package levkaantonov.com.study.telegaclone.ui.screens.base

import androidx.fragment.app.Fragment
import levkaantonov.com.study.telegaclone.utils.APP_ACTIVITY

open class BaseFragment(layout: Int) : Fragment(layout) {
    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }

   /* override fun onStop() {
        super.onStop()
        APP_ACTIVITY.mAppDrawer.enableDrawer()
    }*/
}