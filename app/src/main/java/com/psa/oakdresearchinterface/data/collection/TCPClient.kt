package com.psa.oakdresearchinterface.data.collection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.psa.oakdresearchinterface.data.*
import java.io.*
import java.net.InetAddress
import java.net.Socket


/**
 * Constructor of the class. OnMessagedReceived listens for the messages received from server
 */
class TCPClient (private val handleNewImage: (Bitmap)->Unit){

    // Queue of messages to send to the server
    private val serverMsgQueue: MutableList<String> = mutableListOf()

    // sends message received notifications

    // while this is true, the client thread will continue running
    private var mRun = false

    // used to send messages
    private var mBufferOut: PrintWriter? = null
    // used to read messages from the server
    private var streamIn: DataInputStream? = null
    private var socket: Socket? = null



    /**
     * Sends the message entered by client to the server
     *
     * @param message text to send from the client tablet
     */
    private fun sendMessage(message: String?) {
        try {
            if (mBufferOut != null && !mBufferOut!!.checkError()) {
                Log.d(TCP_TAG, "Client - Sending message: '$message'")
                mBufferOut!!.println(message)
                //Log.d(TCP_TAG, "Client - About to Flush: '$message'")
                mBufferOut!!.flush()
            }
        } catch (e: Exception) {
            Log.e(TCP_TAG, "Client - Send Error", e)
        }
    }
    fun queueMessage(message: String){
        serverMsgQueue.add(message)
    }


    private fun awaitServerMsg(): ByteArray {
        Log.i(TCP_TAG,  "Client - Awaiting server message...")

        var availableBytes = streamIn!!.buffered().available()
        while(availableBytes <= 0)
            availableBytes = streamIn!!.buffered().available()
        val serverData = ByteArray(availableBytes)
        streamIn!!.read(serverData, 0, serverData.size)

        handleMessage(serverData)
        return serverData
    }

    /**
     * Close the connection and release the members
     */
    fun stopClient() {
        // send message that we are closing the connection
        mRun = false
        if (mBufferOut != null) {
            mBufferOut!!.flush()
            mBufferOut!!.close()
        }
        if(socket != null)
            socket!!.close()

        streamIn = null
        mBufferOut = null
    }


    fun run() {
        mRun = true
        try {
            val serverAddr: InetAddress = InetAddress.getByName(SERVER_IP)
            Log.i(TCP_TAG, "Client - Connecting...")

            //create a socket to make the connection with the server
            socket = Socket(serverAddr, SERVER_PORT)
            Log.i(TCP_TAG, "Client - Connected to server.")
            try {
                //sends the message to the server
                mBufferOut =
                    PrintWriter(BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())), true)

                //receives the message which the server sends back
                val streamReader = socket!!.getInputStream()
                streamIn = DataInputStream(streamReader)
                // send login confirmation
                sendMessage(HANDSHAKE_MSG)

                awaitServerMsg() // get the connection confirmation (for testing/debugging purposes primarily)

                while(mRun){
                    if(serverMsgQueue.size > 0){ // If messages in the queue, pop the next message, send it, and wait for a response
                        sendMessage(serverMsgQueue[0])
                        serverMsgQueue.removeFirst()
                        awaitServerMsg()
                    }
                }

            } catch (e: Exception) {
                Log.e(TCP_TAG, "Sever - Error", e)
            }
        } catch (e: Exception) {
            Log.e(TCP_TAG, "Client - Error", e)
        }
    }


    private fun handleMessage(fullMsg: ByteArray){
        if(fullMsg.size >= 2){
            val indicator = fullMsg[0]


            if(indicator == STRING_INDICATOR_BYTE){
                val data = ByteArray(fullMsg.size-1)
                for(i in data.indices){
                    data[i]=fullMsg[i+1]
                }
                Log.i(TCP_TAG, "Client - Received string message (len ${data.size}): ${String(data)}")

                when(String(data)){
                    START_CONFIRMATION, UNPAUSE_CONFIRMATION -> { queueMessage(IMG_REQUEST_MSG) } // request an image to start the image request back and forth
                    STOP_CONFIRMATION, PAUSE_CONFIRMATION -> { // when stopping, remove all image requests from the message queue
                        for(i in serverMsgQueue.size-1 downTo 0) // traverse backwards to prevent skipping items
                            if(serverMsgQueue[i] == IMG_REQUEST_MSG)
                                serverMsgQueue.removeAt(i)
                    }
                }
            }
            else if(indicator == IMG_INDICATOR_BYTE){
                Log.i(TCP_TAG, "Client - Received image data of size: ${fullMsg.size-1} bytes")
                val bitmap = BitmapFactory.decodeByteArray(fullMsg, 1, fullMsg.size-1) // make image into 2d img from byte map
                handleNewImage(bitmap) // handle img
                queueMessage(IMG_REQUEST_MSG) // request another image from the server once this one is handled
            }
        }
        else{
            Log.i(TCP_TAG, "Client - Received empty message")
        }
    }
}