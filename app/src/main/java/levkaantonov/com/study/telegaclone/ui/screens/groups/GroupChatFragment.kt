package levkaantonov.com.study.telegaclone.ui.screens.groups

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.choice_upload.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.fragment_single_chat.view.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.*
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.models.UserModel
import levkaantonov.com.study.telegaclone.ui.screens.base.BaseFragment
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.AppViewFactory
import levkaantonov.com.study.telegaclone.ui.screens.main_list.MainListFragment
import levkaantonov.com.study.telegaclone.utils.*

class GroupChatFragment(private val group: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: GroupChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<*>

    private var mCountMsg = 15
    private var mIsScrolling: Boolean = false
    private var mSmoothScrollToPosition = true

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottom_sheet_choice)
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)

        chat_input_message.addTextChangedListener(AppTextWatcher {
            val msg = chat_input_message.text.toString()
            if (msg.isEmpty()) {
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach_file.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
            } else {
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach_file.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })

        chat_btn_attach_file.setOnClickListener { attach() }

        CoroutineScope(Dispatchers.IO).launch {
            chat_btn_voice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //TODO record
                        chat_input_message.hint = getString(R.string.chat_input_hint_record)
                        chat_btn_voice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.primary
                            )
                        )
                        val messageKey = getMessageKey(group.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        //TODO stop record
                        chat_input_message.hint = getString(R.string.chat_input_hint_message)
                        chat_btn_voice.colorFilter = null
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                group.id,
                                TYPE_MESSAGE_VOICE
                            )
                            mSmoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun attach() {
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        btn_attach_file.setOnClickListener { attachFile() }
        btn_attach_image.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }

        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val uri = CropImage.getActivityResult(data).uri
                val messageKey = getMessageKey(group.id)
                uploadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_IMAGE)
                mSmoothScrollToPosition = true
            }
            PICK_FILE_REQUEST_CODE -> {
                val uri = data.data
                val messageKey = getMessageKey(group.id)
                val fileName = getFilenameFromUri(uri!!)
                uploadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_FILE, fileName)
                mSmoothScrollToPosition = true
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_recycler_view
        mAdapter = GroupChatAdapter()

        mRefMessages = REF_DB_ROOT
            .child(NODE_GROUPS)
            .child(group.id)
            .child(NODE_MESSAGES)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChildEventListener {
            val msg = it.getCommonModel()
            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(AppViewFactory.getView(msg)) {
                    mRecyclerView.smoothScrollToPosition(
                        mAdapter.itemCount
                    )
                }
                return@AppChildEventListener
            }
            mAdapter.addItemToTop(AppViewFactory.getView(msg)) {
                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        mRefMessages.limitToLast(mCountMsg).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                println(mRecyclerView.recycledViewPool.getRecycledViewCount(0))
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMsg += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMsg).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUser = REF_DB_ROOT.child(NODE_USERS).child(group.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        chat_btn_send_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val msg = chat_input_message.text.toString()
            if (msg.isEmpty()) {
                showToast("Введите сообщение")
            } else {
                sendMessageToGroup(msg, group.id, TYPE_MESSAGE_TEXT) {
                    chat_input_message.setText("")
                }
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_info_fullname.text = group.fullname
        } else {
            mToolbarInfo.toolbar_info_fullname.text = mReceivingUser.fullname
        }

        mToolbarInfo.toolbar_info_image.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.toolbar_info_state.text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(group.id){
                showToast(getString(R.string.chat_cleared))
                replaceFragment(MainListFragment())
            }
            R.id.menu_delete_chat -> deleteChat(group.id){
                showToast(getString(R.string.chat_deleted))
                replaceFragment(MainListFragment())
            }
        }
        return true
    }
}