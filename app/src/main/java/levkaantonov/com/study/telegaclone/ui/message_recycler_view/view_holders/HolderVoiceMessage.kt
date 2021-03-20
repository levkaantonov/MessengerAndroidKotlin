package levkaantonov.com.study.telegaclone.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_voice.view.*
import levkaantonov.com.study.telegaclone.database.CURRENT_UID
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.MessageView
import levkaantonov.com.study.telegaclone.utils.AppVoicePlayer
import levkaantonov.com.study.telegaclone.utils.asTime

class HolderVoiceMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    private val mAppVoicePlayer = AppVoicePlayer()
    private val blockUserVoiceMessage: ConstraintLayout = view.block_user_message_voice
    private val chatUserMessageVoiceBtnPlay: ImageView = view.chat_user_btn_play
    private val chatUserMessageVoiceBtnStop: ImageView = view.chat_user_btn_stop
    private val chatUserMessageVoiceMessageTime: TextView = view.chat_user_message_voice_time

    private val blockReceivedVoiceMessage: ConstraintLayout = view.block_received_message_voice
    private val chatReceivedVoiceBtnPlay: ImageView = view.chat_received_btn_play
    private val chatReceivedVoiceBtnStop: ImageView = view.chat_received_btn_stop
    private val chatReceivedVoiceMessageTime: TextView = view.chat_received_message_voice_time

    override fun drawMessage(msg: MessageView) {
        blockUserVoiceMessage.visibility = View.GONE
        blockReceivedVoiceMessage.visibility = View.GONE
        if (msg.from == CURRENT_UID) {
            chatUserMessageVoiceMessageTime.text = msg.timestamp.asTime()
            blockUserVoiceMessage.visibility = View.VISIBLE
            return
        }

        chatReceivedVoiceMessageTime.text = msg.timestamp.asTime()
        blockReceivedVoiceMessage.visibility = View.VISIBLE
    }

    override fun onAttach(view: MessageView) {
        mAppVoicePlayer.preparePlayer()
         if(view.from == CURRENT_UID){
             chatUserMessageVoiceBtnPlay.setOnClickListener {
                 chatUserMessageVoiceBtnPlay.visibility = View.GONE
                 chatUserMessageVoiceBtnStop.visibility = View.VISIBLE

                 chatUserMessageVoiceBtnStop.setOnClickListener {
                    stop {
                        chatUserMessageVoiceBtnStop.setOnClickListener(null)
                        chatUserMessageVoiceBtnPlay.visibility = View.VISIBLE
                        chatUserMessageVoiceBtnStop.visibility = View.GONE
                    }
                 }

                 play(view){
                     chatUserMessageVoiceBtnPlay.visibility = View.VISIBLE
                     chatUserMessageVoiceBtnStop.visibility = View.GONE
                 }
             }
             return
         }

        chatReceivedVoiceBtnPlay.setOnClickListener{
            chatReceivedVoiceBtnPlay.visibility = View.GONE
            chatReceivedVoiceBtnStop.visibility = View.VISIBLE

            chatReceivedVoiceBtnStop.setOnClickListener {
                stop {
                    chatReceivedVoiceBtnStop.setOnClickListener(null)
                    chatReceivedVoiceBtnPlay.visibility = View.VISIBLE
                    chatReceivedVoiceBtnStop.visibility = View.GONE
                }
            }

            play(view){
                chatReceivedVoiceBtnPlay.visibility = View.VISIBLE
                chatReceivedVoiceBtnStop.visibility = View.GONE
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl){
            function()
        }
    }

    private fun stop(function: () -> Unit){
        mAppVoicePlayer.stop { function() }
    }

    override fun onDetach() {
        chatUserMessageVoiceBtnPlay.setOnClickListener(null)
        chatReceivedVoiceBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}