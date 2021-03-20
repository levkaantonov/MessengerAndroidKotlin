package levkaantonov.com.study.telegaclone.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_image.view.*
import levkaantonov.com.study.telegaclone.database.CURRENT_UID
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.MessageView
import levkaantonov.com.study.telegaclone.utils.asTime
import levkaantonov.com.study.telegaclone.utils.downloadAndSetImage

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserImageMessage: ConstraintLayout = view.block_user_image_message
    private val chatUserImageMessage: ImageView = view.chat_user_image_message
    private val chatUserImageMessageTime: TextView = view.chat_user_image_message_time

    private val blockReceivedImageMessage: ConstraintLayout = view.block_received_image_message
    private val chatReceivedImageMessage: ImageView = view.chat_received_image_message
    private val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time


    override fun drawMessage(msg: MessageView) {
        blockUserImageMessage.visibility = View.GONE
        blockReceivedImageMessage.visibility = View.GONE
        if (msg.from == CURRENT_UID) {
            chatUserImageMessage.downloadAndSetImage(msg.fileUrl)
            chatUserImageMessageTime.text = msg.timestamp.asTime()
            blockUserImageMessage.visibility = View.VISIBLE
            return
        }

        chatReceivedImageMessage.downloadAndSetImage(msg.fileUrl)
        chatReceivedImageMessageTime.text = msg.timestamp.asTime()
        blockReceivedImageMessage.visibility = View.VISIBLE
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}