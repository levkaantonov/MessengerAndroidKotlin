package levkaantonov.com.study.telegaclone.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main_list.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.*
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.utils.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainLst = REF_DB_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DB_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DB_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.app_name)
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycler_view
        mAdapter = MainListAdapter()

        mRefMainLst
            .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                mListItems = snapshot.children.map { it.getCommonModel() }
                mListItems.forEach { model ->
                    when(model.type){
                         TYPE_CHAT -> showChat(model)
                        TYPE_GROUP -> showGroup(model)
                    }


                }
            })

        mRecyclerView.adapter = mAdapter
    }

    private fun showGroup(model: CommonModel) {
        REF_DB_ROOT.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                val newModel = dataSnapshot.getCommonModel()

                REF_DB_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        val messages = it.children.map { it.getCommonModel() }
                        if(messages.isEmpty()){
                            newModel.lastMessage = getString(R.string.chat_cleared)
                        }else{
                            newModel.lastMessage = messages[0].text
                        }

                        newModel.type = TYPE_GROUP
                        mAdapter.updateListItems(newModel)
                    })
            })
    }

    private fun showChat(model: CommonModel) {
        mRefUsers
            .child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                val newModel = dataSnapshot.getCommonModel()

                mRefMessages
                    .child(model.id)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        val messages = it.children.map { it.getCommonModel() }

                        if(messages.isEmpty()){
                            newModel.lastMessage = getString(R.string.chat_cleared)
                        }else{
                            newModel.lastMessage = messages[0].text
                        }

                        if(newModel.fullname.isEmpty()){
                            newModel.fullname = newModel.phone
                        }

                        newModel.type = TYPE_CHAT
                        mAdapter.updateListItems(newModel)
                    })
            })
    }
}