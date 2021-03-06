package levkaantonov.com.study.telegaclone.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import levkaantonov.com.study.telegaclone.MainActivity
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.utils.AUTH
import levkaantonov.com.study.telegaclone.utils.replaceActivity

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                (activity as MainActivity).replaceActivity(RegisterActivity::class.java)
            }
        }
        return true
    }
}