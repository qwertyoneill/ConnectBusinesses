package com.tiagovaz.connectbusinesses.data.network

import okhttp3.*
import org.json.JSONObject

class ChatRealtimeClient(
    private val token: String,
    private val conversationId: Int,
    private val onNewMessage: (String) -> Unit
) {

    private val client = OkHttpClient()
    private var socket: WebSocket? = null

    fun connect() {

        val request = Request.Builder()
            .url("wss://directus.qwertyoneill.xyz/websocket")
            .build()

        socket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {

                val auth = JSONObject()
                auth.put("type", "auth")
                auth.put("access_token", token)

                webSocket.send(auth.toString())

                val subscribe = JSONObject()
                subscribe.put("type", "subscribe")
                subscribe.put("collection", "conversation_messages")

                val filter = JSONObject()
                filter.put("_eq", conversationId)

                val query = JSONObject()
                val conversationFilter = JSONObject()
                conversationFilter.put("conversation_id", filter)

                query.put("filter", conversationFilter)

                subscribe.put("query", query)

                webSocket.send(subscribe.toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {

                try {

                    val json = JSONObject(text)

                    if (json.has("data")) {

                        val message = json.getJSONObject("data")
                        val messageText = message.getString("message_text")

                        onNewMessage(messageText)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun disconnect() {
        socket?.close(1000, "chat closed")
    }
}