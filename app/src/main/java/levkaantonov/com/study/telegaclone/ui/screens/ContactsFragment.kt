package levkaantonov.com.study.telegaclone.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.database.*
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.ui.screens.single_chat.SingleChatFragment
import levkaantonov.com.study.telegaclone.utils.*

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersLister: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        println()
        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
        println()
    }

    private fun initRecyclerView() {
        mRecyclerView = contacts_recycler_view
        mRefContacts = REF_DB_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DB_ROOT.child(NODE_USERS).child(model.id)
                mRefUsersLister = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty())
                        holder.name.text = model.fullname
                    else
                        holder.name.text = contact.fullname

                    holder.state.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.name.text = contact.fullname
                    holder.itemView.setOnClickListener {
                            replaceFragment(SingleChatFragment(model))
                    }
                }

                mRefUsers.addValueEventListener(mRefUsersLister)
                mapListeners[mRefUsers] = mRefUsersLister
            }
        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.contact_fullname
        val state: TextView = view.contact_state
        val photo: CircleImageView = view.contact_photo
    }
}

