package levkaantonov.com.study.telegaclone.ui.screens.settings

import kotlinx.android.synthetic.main.fragment_change_bio.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.USER
import levkaantonov.com.study.telegaclone.database.updateBioToDb
import levkaantonov.com.study.telegaclone.ui.screens.base.BaseChangeFragment

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        val newBio = settings_input_bio.text.toString()
        updateBioToDb(newBio)
    }
}