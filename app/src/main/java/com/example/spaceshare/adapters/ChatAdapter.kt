package com.example.spaceshare.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.databinding.ChatItemBinding
import com.example.spaceshare.models.Chat
import com.example.spaceshare.utils.ImageLoaderUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat

class ChatAdapter(
    private val userRepo: UserRepository,
    private val itemClickListener: ItemClickListener
) : ListAdapter<Chat, ChatAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)
    }

    inner class ViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            // Bind the chat data to the views
            if (chat.photoURL != null) {
                ImageLoaderUtil.loadImageIntoView(binding.chatImageView, chat.photoURL, false)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val hostUser = chat.hostId?.let { userRepo.getUserById(it) }
                withContext(Dispatchers.Main) {
                    if (hostUser != null) {
                        binding.chatOwner.text = hostUser.firstName + " " + hostUser.lastName
                    }
                }
            }

            binding.chatItemTitle.text = chat.title ?: "Untitled"

            val lastMessage = chat.lastMessage
            if (lastMessage != null) {
                var lastUpdate = ""
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                // Add who sent the last message
                if (lastMessage.senderId != currentUserId)
                    lastUpdate += lastMessage.senderName
                else
                    lastUpdate += "You"

                // Add what the last message was
                if (lastMessage.text != null)
                    lastUpdate += ": " + lastMessage.text
                else
                    lastUpdate += " sent an image"

                binding.chatLastUpdate.text = lastUpdate

                binding.lastUpdateTime.text = getLastUpdateTimeString(lastMessage.timestamp)
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val notHostMemberId = chat.members.filterNot { memberId ->
                        memberId == chat.hostId
                    }.first()
                    val notHostUser = userRepo.getUserById(notHostMemberId)

                    withContext(Dispatchers.Main) {
                        if (notHostUser != null) {
                            binding.chatLastUpdate.text =
                                "${notHostUser.firstName} ${notHostUser.lastName} created the chat"

                            if (chat.createdAt != null) {
                                binding.lastUpdateTime.text =
                                    getLastUpdateTimeString(chat.createdAt.toDate().time)
                            } else {
                                binding.lastUpdateTime.text = ""
                            }
                        }
                    }
                }
            }

            // Set click listeners
            binding.chatItemContainer.setOnClickListener {
                itemClickListener.onItemClick(chat)
            }
        }

        private fun getLastUpdateTimeString(timestamp: Long): String {
            // Generate last update time string
            if (DateUtils.isToday(timestamp)) {
                var lastUpdateTimeString =
                    DateUtils.getRelativeTimeSpanString(timestamp)
                if (lastUpdateTimeString == "0 minutes ago") {
                    lastUpdateTimeString = "Just now"
                }
                return lastUpdateTimeString.toString()
            } else {
                val simpleDateFormat = SimpleDateFormat("MMM dd")
                val lastMessageTimeFormatted =
                    simpleDateFormat.format(Date(timestamp))
                return lastMessageTimeFormatted
            }
        }
    }


    interface ItemClickListener {
        fun onItemClick(chat: Chat)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
}