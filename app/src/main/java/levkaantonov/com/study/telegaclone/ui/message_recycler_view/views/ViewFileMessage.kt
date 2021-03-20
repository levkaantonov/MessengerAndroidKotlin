package levkaantonov.com.study.telegaclone.ui.message_recycler_view.views

data class ViewFileMessage(
    override val id: String,
    override val from: String,
    override val timestamp: String,
    override val fileUrl: String,
    override val text: String = ""
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_FILE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}