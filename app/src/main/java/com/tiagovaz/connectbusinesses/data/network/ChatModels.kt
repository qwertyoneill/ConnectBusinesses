package com.tiagovaz.connectbusinesses.data.network

data class ConversationsResponse(
    val data: List<ConversationItem>
)

data class ConversationItem(
    val conversation_id: Int,
    val match_id: Int,
    val user_id: String,
    val other_user_id: String,
    val other_first_name: String?,
    val other_last_name: String?,
    val other_email: String?,
    val other_avatar: String?,
    val lead_id: Int,
    val lead_name: String?,
    val last_message_text: String?,
    val last_message_at: String?,
    val last_message_sender_id: String?,
    val unread_count: Int,
    val updated_at: String?
)

data class ConversationMessagesResponse(
    val data: List<ConversationMessageItem>
)

data class ConversationMessageItem(
    val id: Int,
    val conversation_id: Int,
    val sender_user_id: String,
    val message_text: String,
    val created_at: String,
    val edited_at: String?,
    val deleted_at: String?
)

data class SendMessageRequest(
    val message: String
)

data class MarkConversationReadResponse(
    val success: Boolean,
    val conversation_id: Int
)