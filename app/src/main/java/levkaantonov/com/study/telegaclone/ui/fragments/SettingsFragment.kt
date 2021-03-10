package levkaantonov.com.study.telegaclone.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import levkaantonov.com.study.telegaclone.MainActivity
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.activities.RegisterActivity
import levkaantonov.com.study.telegaclone.utils.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        settings_bio.text = USER.bio
        settings_full_name.text = USER.fullname
        settings_phone_number.text = USER.phone
        settings_user_status.text = USER.status
        settings_user_name.text = USER.name
        settings_btn_change_user_name.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        settings_btn_change_bio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        settings_change_photo.setOnClickListener { changeUserPhoto() }
    }

    private fun changeUserPhoto() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK
            && data !== null){
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)
            path.putFile(uri).addOnCompleteListener { putFileTask ->
                if(putFileTask.isSuccessful){
                    path.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                        if(downloadUrlTask.isSuccessful){
                            val photoUrl = downloadUrlTask.result.toString()
                            REF_DB_ROOT
                                .child(NODE_USERS)
                                .child(CURRENT_UID)
                                .child(CHILD_PHOTO_URL)
                                .setValue(photoUrl)
                                .addOnCompleteListener {setValueTask ->
                                    if(setValueTask.isSuccessful){
                                        settings_user_photo.downloadAndSetImage(photoUrl)
                                        USER.photoUrl = photoUrl
                                        showToast(getString(R.string.toast_data_updated))

                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                APP_ACTIVITY.replaceActivity(RegisterActivity::class.java)
            }
            R.id.settings_menu_change_name -> {
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }
}