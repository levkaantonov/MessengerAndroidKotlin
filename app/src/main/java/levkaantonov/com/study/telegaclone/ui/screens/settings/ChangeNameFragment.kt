package levkaantonov.com.study.telegaclone.ui.screens.settings

import kotlinx.android.synthetic.main.fragment_change_name.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.USER
import levkaantonov.com.study.telegaclone.database.updateFullName
import levkaantonov.com.study.telegaclone.ui.screens.base.BaseChangeFragment
import levkaantonov.com.study.telegaclone.utils.*

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {
    override fun onResume() {
        super.onResume()
        initFullName()
    }

    private fun initFullName() {
        if (USER.fullname.isNotEmpty()) {
            val idx = USER.fullname.indexOf(' ')
            val name = USER.fullname.substring(0, idx)
            val surname = USER.fullname.substring(idx, USER.fullname.length)
            settings_input_name.setText(name)
            settings_input_surname.setText(surname)
        }
    }

    override fun change() {
        val name = settings_input_name.text.toString()
        val surname = settings_input_surname.text.toString()
        if(name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_is_empty))
            return
        }
        updateFullName("$name $surname")
    }
}