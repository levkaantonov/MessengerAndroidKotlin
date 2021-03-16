package levkaantonov.com.study.telegaclone.ui.fragments.single_chat

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.fragment_single_chat.view.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.models.UserModel
import levkaantonov.com.study.telegaclone.ui.fragments.BaseFragment
import levkaantonov.com.study.telegaclone.utils.*

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager

    private var mCountMsg = 15
    private var mIsScrolling: Boolean = false
    private var mSmoothScrollToPosition = true

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
    }

    private fun initFields() {
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_recycler_view
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DB_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChildEventListener {
            val msg = it.getCommonModel()
            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(msg) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
                return@AppChildEventListener
            }
            mAdapter.addItemToTop(msg) {
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

        mSwipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
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
        mRefUser = REF_DB_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val msg = chat_input_message.text.toString()
            if (msg.isEmpty()) {
                showToast("Введите сообщение")
            } else {
                sendMessage(msg, contact.id, TYPE_TXT) {
                    chat_input_message.setText("")
                }
            }
        }
    }


    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_info_fullname.text = contact.fullname
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
}