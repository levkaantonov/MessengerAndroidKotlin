package levkaantonov.com.study.telegaclone.ui.message_recycler_view.view_holders

import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}