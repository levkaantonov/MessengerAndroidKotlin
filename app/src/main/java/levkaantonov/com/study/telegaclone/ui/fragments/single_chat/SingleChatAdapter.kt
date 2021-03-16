package levkaantonov.com.study.telegaclone.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.service.Common
import kotlinx.android.synthetic.main.message_item.view.*
import levkaantonov.com.study.telegaclone.R
import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.utils.CURRENT_UID
import levkaantonov.com.study.telegaclone.utils.DiffUtilCallback
import levkaantonov.com.study.telegaclone.utils.asTime
import java.text.SimpleDateFormat
import java.util.*

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {
    private var mListMessagesCache = mutableListOf<CommonModel>()
    private lateinit var mDiffResult: DiffUtil.DiffResult

    class SingleChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blockUserMessage: ConstraintLayout = view.block_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_message_time

        val blockReceivedMessage: ConstraintLayout = view.block_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        val chatReceivedMessageTime: TextView = view.chat_message_received_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        val msg = mListMessagesCache[position]
        if (msg.from == CURRENT_UID) {
            holder.chatUserMessage.text = msg.text
            holder.chatUserMessageTime.text = msg.timestamp.toString().asTime()

            holder.blockUserMessage.visibility = View.VISIBLE
            return
        }

        holder.chatReceivedMessage.text = msg.text
        holder.chatReceivedMessageTime.text = msg.timestamp.toString().asTime()
        holder.blockReceivedMessage.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    fun setList(list: List<CommonModel>) {

//        notifyDataSetChanged()
    }

    fun addItemToBottom(item: CommonModel, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: CommonModel, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timestamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}
