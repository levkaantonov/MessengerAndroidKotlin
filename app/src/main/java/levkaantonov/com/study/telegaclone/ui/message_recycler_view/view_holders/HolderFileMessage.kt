package levkaantonov.com.study.telegaclone.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_file.view.*
import kotlinx.android.synthetic.main.message_item_voice.view.*
import levkaantonov.com.study.telegaclone.database.CURRENT_UID
import levkaantonov.com.study.telegaclone.database.getFileFromStorage
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.MessageView
import levkaantonov.com.study.telegaclone.utils.WRITE_FILES
import levkaantonov.com.study.telegaclone.utils.asTime
import levkaantonov.com.study.telegaclone.utils.checkPermission
import levkaantonov.com.study.telegaclone.utils.showToast
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserFileMessage: ConstraintLayout = view.block_user_message_file
    private val chatUserMessageFileBtnDownload: ImageView = view.chat_user_btn_download
    private val chatUserMessageFilename: TextView = view.chat_user_file_name
    private val chatUserMessageFileMessageTime: TextView = view.chat_user_message_file_time
    private val chatUserMessageProgressBar: ProgressBar = view.chat_user_progress_bar

    private val blockReceivedFileMessage: ConstraintLayout = view.block_received_message_file
    private val chatReceivedFileBtnDownload: ImageView = view.chat_received_btn_download
    private val chatReceivedMessageFilename: TextView = view.chat_received_file_name
    private val chatReceivedFileMessageTime: TextView = view.chat_received_message_file_time
    private val chatReceivedProgressBar: ProgressBar = view.chat_received_progress_bar

    override fun drawMessage(msg: MessageView) {
        blockUserFileMessage.visibility = View.GONE
        blockReceivedFileMessage.visibility = View.GONE

        if (msg.from == CURRENT_UID) {
            chatUserMessageFileMessageTime.text = msg.timestamp.asTime()
            chatUserMessageFilename.text = msg.text
            blockUserFileMessage.visibility = View.VISIBLE
            return
        }

        chatReceivedFileMessageTime.text = msg.timestamp.asTime()
        chatReceivedMessageFilename.text = msg.text

        blockReceivedFileMessage.visibility = View.VISIBLE

    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserMessageFileBtnDownload.setOnClickListener { clickToBtnFile(view) }
            return
        }

        chatReceivedFileBtnDownload.setOnClickListener { clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserMessageFileBtnDownload.visibility = View.INVISIBLE
            chatUserMessageProgressBar.visibility = View.VISIBLE
        }else{
            chatReceivedFileBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try {
            if(!checkPermission(WRITE_FILES)){
                return
            }

            file.createNewFile()
            getFileFromStorage(file, view.fileUrl){
                if (view.from == CURRENT_UID) {
                    chatUserMessageFileBtnDownload.visibility = View.VISIBLE
                    chatUserMessageProgressBar.visibility = View.INVISIBLE
                }else{
                    chatReceivedFileBtnDownload.visibility = View.VISIBLE
                    chatReceivedProgressBar.visibility = View.INVISIBLE
                }
            }

        } catch (e: Exception) {
            if (view.from == CURRENT_UID) {
                chatUserMessageProgressBar.visibility = View.INVISIBLE
                chatUserMessageFileBtnDownload.visibility = View.VISIBLE
            }else{
                chatReceivedProgressBar.visibility = View.INVISIBLE
                chatReceivedFileBtnDownload.visibility = View.VISIBLE
            }
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserMessageFileBtnDownload.setOnClickListener(null)
        chatReceivedFileBtnDownload.setOnClickListener(null)
    }
}