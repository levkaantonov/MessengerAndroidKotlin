package levkaantonov.com.study.telegaclone.ui.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_contacts_item.view.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.ui.screens.single_chat.SingleChatFragment
import levkaantonov.com.study.telegaclone.utils.downloadAndSetImage
import levkaantonov.com.study.telegaclone.utils.replaceFragment
import levkaantonov.com.study.telegaclone.utils.showToast

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {
    private val listItems = mutableListOf<CommonModel>()

    class AddContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.add_contacts_item_name
        val itemLastMessage: TextView = view.add_contacts_last_message
        val itemPhoto: CircleImageView = view.add_contacts_item_photo
        val itemChoice: CircleImageView = view.add_contacts_item_choice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item, parent, false)
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener {
            val contact = listItems[holder.adapterPosition]
            if(!contact.choice){
                holder.itemChoice.visibility = View.VISIBLE
                contact.choice = true
                AddContactsFragment.listContacts.add(contact)
                return@setOnClickListener
            }

            holder.itemChoice.visibility = View.INVISIBLE
            contact.choice = false
            AddContactsFragment.listContacts.remove(contact)
        }
        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}