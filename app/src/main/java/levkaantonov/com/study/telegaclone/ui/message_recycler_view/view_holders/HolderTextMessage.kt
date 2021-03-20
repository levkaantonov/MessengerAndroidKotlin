package levkaantonov.com.study.telegaclone.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_text.view.*
import levkaantonov.com.study.telegaclone.database.CURRENT_UID
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.MessageView
import levkaantonov.com.study.telegaclone.utils.asTime

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserMessage: ConstraintLayout = view.block_user_message
    private val chatUserMessage: TextView = view.chat_user_message
    private val chatUserMessageTime: TextView = view.chat_user_message_time

    private val blockReceivedMessage: ConstraintLayout = view.block_received_message
    private val chatReceivedMessage: TextView = view.chat_received_message
    private val chatReceivedMessageTime: TextView = view.chat_message_received_time

    override fun drawMessage(
        msg: MessageView
    ) {
        blockUserMessage.visibility = View.GONE
         blockReceivedMessage.visibility = View.GONE
        if (msg.from == CURRENT_UID) {
            chatUserMessage.text = msg.text
            chatUserMessageTime.text = msg.timestamp.asTime()
            blockUserMessage.visibility = View.VISIBLE
            return
        }

        chatReceivedMessage.text = msg.text
        chatReceivedMessageTime.text = msg.timestamp.asTime()
        blockReceivedMessage.visibility = View.VISIBLE
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}