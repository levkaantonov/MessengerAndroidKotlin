package levkaantonov.com.study.telegaclone.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_list_item.view.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.ui.screens.groups.GroupChatFragment
import levkaantonov.com.study.telegaclone.ui.screens.single_chat.SingleChatFragment
import levkaantonov.com.study.telegaclone.utils.TYPE_CHAT
import levkaantonov.com.study.telegaclone.utils.TYPE_GROUP
import levkaantonov.com.study.telegaclone.utils.downloadAndSetImage
import levkaantonov.com.study.telegaclone.utils.replaceFragment

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {
    private val listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemName :TextView = view.main_list_item_name
        val itemLastMessage :TextView = view.main_list_last_message
        val itemPhoto :CircleImageView = view.main_list_item_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener{
            val model = listItems[holder.adapterPosition]
            when(model.type){
                TYPE_CHAT ->replaceFragment(SingleChatFragment(model))
                TYPE_GROUP ->replaceFragment(GroupChatFragment(model))
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item: CommonModel){
        listItems.add(item)
        notifyItemInserted(listItems.size )
    }
}