package levkaantonov.com.study.telegaclone.ui.message_recycler_view.views

import levkaantonov.com.study.telegaclone.models.CommonModel
import levkaantonov.com.study.telegaclone.utils.TYPE_MESSAGE_FILE
import levkaantonov.com.study.telegaclone.utils.TYPE_MESSAGE_IMAGE
import levkaantonov.com.study.telegaclone.utils.TYPE_MESSAGE_VOICE

class AppViewFactory {
    companion object{
        fun getView(message: CommonModel): MessageView{
            return when(message.type){
                TYPE_MESSAGE_IMAGE -> ViewImageMessage(
                    message.id,
                    message.from,
                    message.timestamp.toString(),
                    message.fileUrl
                )
                TYPE_MESSAGE_FILE -> ViewFileMessage(
                    message.id,
                    message.from,
                    message.timestamp.toString(),
                    message.fileUrl,
                    message.text
                )
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(
                    message.id,
                    message.from,
                    message.timestamp.toString(),
                    message.fileUrl
                )
                else -> ViewTextMessage(
                    message.id,
                    message.from,
                    message.timestamp.toString(),
                    message.fileUrl,
                    message.text
                )
            }
        }
    }
}