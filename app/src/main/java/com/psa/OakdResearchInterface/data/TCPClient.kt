package com.psa.OakdResearchInterface.data

import android.util.Log
import java.io.*
import java.net.InetAddress
import java.net.Socket


/**
 * Constructor of the class. OnMessagedReceived listens for the messages received from server
 */
class TCPClient (listener: OnMessageReceived){

    // message to send to the server
    private var mServerMessage: String? = null

    // sends message received notifications
    private var mMessageListener: OnMessageReceived? = null

    // while this is true, the server will continue running
    private var mRun = false

    // used to send messages
    private var mBufferOut: PrintWriter? = null

    // used to read messages from the server
    private var mBufferIn: BufferedReader? = null


    init {
        mMessageListener = listener
    }


    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    fun sendMessage(message: String?) {
        if (mBufferOut != null && !mBufferOut!!.checkError()) {
            Log.d(TCP_TAG, "Client - Sending message: '$message'")
            mBufferOut!!.println(message)
            mBufferOut!!.flush()
        }
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
        mMessageListener = null
        mBufferIn = null
        mBufferOut = null
        mServerMessage = null
    }


    fun run() {
        mRun = true
        try {
            //here you must put your computer's IP address.
            val serverAddr: InetAddress = InetAddress.getByName(SERVER_IP)
            Log.i(TCP_TAG, "Client - Connecting...")

            //create a socket to make the connection with the server
            val socket = Socket(serverAddr, SERVER_PORT)
            Log.i(TCP_TAG, "Client - Connected to server.")
            try {
                //sends the message to the server
                mBufferOut =
                    PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)

                //receives the message which the server sends back
                val streamReader = InputStreamReader(socket.getInputStream())
                mBufferIn = BufferedReader(streamReader)
                // send login name
                sendMessage(HANDSHAKE_MSG)

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    Log.d(TCP_TAG,  "Client - Awaiting message...")
                    var message = socket.getInputStream().readBytes()
                    Log.d(TCP_TAG, "Client - Received: $message")
                    //mServerMessage = mBufferIn!!.readLine()
                    //Log.d(TCP_TAG, "Client - Received: " + mServerMessage!!)
                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener!!.messageReceived(mServerMessage)
                    }
                }
                //Log.e(TCP_TAG, "C - Received Message From Server: '$mServerMessage'")
            } catch (e: Exception) {
                Log.e(TCP_TAG, "Sever - Error", e)
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close()
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