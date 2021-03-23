package levkaantonov.com.study.telegaclone.ui.screens.groups

import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_contacts.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.*
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.ui.screens.base.BaseFragment
import levkaantonov.com.study.telegaclone.utils.*

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefContactsList = REF_DB_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
    private val mRefUsers = REF_DB_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DB_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        listContacts.clear()
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.add_into_group)
        hideKeyboard()
        initRecyclerView()

        add_contacts_btn_next.setOnClickListener{
            if(listContacts.isEmpty()){
                showToast(getString(R.string.warning_add_contact))
                return@setOnClickListener
            }
            replaceFragment(CreateGroupFragment(listContacts))
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = add_contacts_recycler_view
        mAdapter = AddContactsAdapter()

        mRefContactsList
            .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                mListItems = snapshot.children.map { it.getCommonModel() }
                mListItems.forEach { model ->

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
                                    mAdapter.updateListItems(newModel)
                                })
                        })
                }
            })

        mRecyclerView.adapter = mAdapter
    }

    companion object{
        val listContacts  = mutableListOf<CommonModel>()
    }
}