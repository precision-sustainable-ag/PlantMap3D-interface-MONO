package com.psa.oakdresearchinterface.data

import android.util.Log
import java.io.*
import java.net.InetAddress
import java.net.Socket


/**
 * Constructor of the class. OnMessagedReceived listens for the messages received from server
 */
class TCPClient (listener: OnMessageReceived){

    // Queue of messages to send to the server
    private val serverMsgQueue: MutableList<String> = mutableListOf()

    // sends message received notifications
    private var mMessageListener: OnMessageReceived? = null

    // while this is true, the client thread will continue running
    private var mRun = false

    // used to send messages
    private var mBufferOut: PrintWriter? = null
    // used to read messages from the server
    private var mBufferIn: BufferedReader? = null
    private var socket: Socket? = null



    init {
        mMessageListener = listener
    }



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
                Log.d(TCP_TAG, "Client - About to Flush: '$message'")
                mBufferOut!!.flush()
            }
        } catch (e: Exception) {
            Log.e(TCP_TAG, "Client - Send Error", e)
        }
    }
    fun queueMessage(message: String){
        serverMsgQueue.add(message)
    }


    private fun awaitServerMsg(): String {
        Log.d(TCP_TAG,  "Client - Awaiting message...")
        var message = socket!!.getInputStream().readBytes()
        //Log.d(TCP_TAG, "Client - Received: $message")
        val serverMessage = mBufferIn!!.readLine()
        Log.d(TCP_TAG, "Client - Received: " + serverMessage!!)

        mMessageListener!!.messageReceived(serverMessage)

        return serverMessage!!
    }

    /**
     * Close the connection and release the members
     */
    fun stopClient() {
        // send message that we are closing the connection
        sendMessage(CLOSE_MSG)
        mRun = false
        if (mBufferOut != null) {
            mBufferOut!!.flush()
            mBufferOut!!.close()
        }
        if(socket != null)
            socket!!.close()

        mMessageListener = null
        mBufferIn = null
        mBufferOut = null
    }


    fun run() {
        mRun = true // TODO: MAKE CLIENT RECEIVE MESSAGES PROPERLY
        try {
            //here you must put your computer's IP address.
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
                val streamReader = InputStreamReader(socket!!.getInputStream())
                mBufferIn = BufferedReader(streamReader)
                // send login name
                sendMessage(HANDSHAKE_MSG)

                val handshakeMsg = awaitServerMsg() // get the connection confirmation (for testing/debugging purposes primarily)
                Log.e(TCP_TAG, "Client - Received Server Handshake: '$handshakeMsg'")

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

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MainActivity
    //class at on async Task doInBackground
    interface OnMessageReceived {
        fun messageReceived(message: String?)
    }
}