package com.example.spaceshare.data.repository

import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Message
import com.google.firebase.database.DatabaseReference

interface MessagesRepository {

    // Creates a chat with the members set to the memberIds
    suspend fun createChat(
        title: String,
        photoURL: String,
        hostId: String,
        associatedListingId: String,
        memberIds: List<String>
    ): Chat

    // Gets all the chats where the members are exactly the memberIds provided
    suspend fun getChatsByMemberIds(memberIds: List<String>): List<Chat>

    // Gets all the chats a user is a member of
    suspend fun getChatsByUserId(userId: String): List<Chat>

    suspend fun setLastMessage(chatId: String, lastMessage: Message)

    suspend fun getChatById(chatId: String): Chat?

    suspend fun deleteChatsByAssociatedListingId(listingId: String)

    fun getBaseMessagesRef(): DatabaseReference
}