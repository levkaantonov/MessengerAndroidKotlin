package levkaantonov.com.study.telegaclone.ui.screens.groups

import android.os.Message
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.view_holders.*
import levkaantonov.com.study.telegaclone.ui.message_recycler_view.views.MessageView

class GroupChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListMessagesCache = mutableListOf<MessageView>()
    private var mListHolders = mutableListOf<MessageHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(mListMessagesCache[position])
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        mListHolders.add((holder as MessageHolder))
        (holder as MessageHolder).onAttach(mListMessagesCache[holder.adapterPosition])
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as MessageHolder).onDetach()
        mListHolders.remove((holder as MessageHolder))
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timestamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        mListHolders.forEach {
            it.onDetach()
        }
    }
}
