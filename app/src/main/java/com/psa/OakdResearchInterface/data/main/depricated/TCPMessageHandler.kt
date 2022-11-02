package com.psa.OakdResearchInterface.data.main.depricated

import android.util.Log
import com.psa.OakdResearchInterface.data.TCP_TAG


class TCPMessageHandler {
    private val messageSendQueue: MutableList<String> = mutableListOf()
    fun queueMsgSend(utf8Message: String){
        Log.d(TCP_TAG, "Message Queued to Send: $utf8Message")
        messageSendQueue.add(utf8Message)
    }
    fun pullNextOutboundMessage(): String {
        if (isMessageQueued()){
            val message: String = messageSendQueue[0]
            messageSendQueue.removeFirst()
            return message
        }
        else{
            Log.e(TCP_TAG, "MESSAGE HANDLER ERROR: Failed attempt to pull queued message from messageSendQueue of size ${messageSendQueue.size}.")
            return "NO MESSAGE QUEUED"
        }
    }
    fun isMessageQueued(): Boolean{
        return messageSendQueue.size >= 1
    }

    fun proccessServerMsg(serverMessage: String){
        Log.d(TCP_TAG, "Message Received From Server: $serverMessage")

        // Insert message handling code here
    }
}