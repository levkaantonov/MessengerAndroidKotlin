package levkaantonov.com.study.telegaclone.ui.fragments

import kotlinx.android.synthetic.main.fragment_change_bio.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.utils.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        val newBio = settings_input_bio.text.toString()
        REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO)
            .setValue(newBio)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    showToast(getString(R.string.toast_data_updated))
                    USER.bio = newBio
                    fragmentManager?.popBackStack()
                }
            }
    }
}