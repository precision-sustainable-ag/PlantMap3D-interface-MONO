package com.psa.oakdresearchinterface.data.collection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.psa.oakdresearchinterface.data.*
import io.ktor.util.date.*
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
    private fun sendMessage(message: String?, debugOutput: Boolean = true) {
        try {
            if (mBufferOut != null && !mBufferOut!!.checkError()) {
                if(debugOutput)
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


    private var msgAwaitCanBlockThread = true
    private fun awaitServerMsg(debugOutput: Boolean = true): ByteArray {
        if(debugOutput)
            Log.d(TCP_TAG,  "Client - Awaiting server message...")

        msgAwaitCanBlockThread = true
        var availableBytes = streamIn!!.buffered().available()
        while(availableBytes <= 0 && msgAwaitCanBlockThread)
            availableBytes = streamIn!!.buffered().available()

        var serverData = ByteArray(0)
        do{
            val newData = ByteArray(availableBytes)
            streamIn!!.read(newData, 0, newData.size)
            serverData += newData
            ioSafeSleep(STREAM_READ_COMPLETE_CHECK_DELAY)
            availableBytes = streamIn!!.buffered().available()
        } while(availableBytes > 0 && msgAwaitCanBlockThread)

        handleMessage(serverData)
        return serverData
    }
    fun clearInboundBuff(debugOutput: Boolean = true){
        var clearedBytes = 0
        var availableBytes = streamIn!!.buffered().available()
        while(availableBytes > 0 && msgAwaitCanBlockThread){
            val dataToVoid = ByteArray(availableBytes)
            streamIn!!.read(dataToVoid, 0, dataToVoid.size)
            clearedBytes += dataToVoid.size
            availableBytes = streamIn!!.buffered().available()
        }
        if(debugOutput)
            Log.d(TCP_TAG, "Client - Cleared $clearedBytes byte(s) from the input buffer")
    }
    fun interruptMessageWait(){
        msgAwaitCanBlockThread = false
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
                Log.i(TCP_TAG, "Client - Connected to server.")


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
                    START_CONFIRMATION, UNPAUSE_CONFIRMATION, IMG_TRANSFER_COMPLETE_MSG -> { queueMessage(IMG_REQUEST_MSG) } // request an image to start the image request back and forth
                    STOP_CONFIRMATION, PAUSE_CONFIRMATION -> { // when stopping, remove all image requests from the message queue
                        for(i in serverMsgQueue.size-1 downTo 0) // traverse backwards to prevent skipping items
                            if(serverMsgQueue[i] == IMG_REQUEST_MSG)
                                serverMsgQueue.removeAt(i)
                    }
                }
            }
            else if(indicator == IMG_INDICATOR_BYTE){
                Log.i(TCP_TAG, "Client - Received image data of size: ${fullMsg.size-1} bytes")
                val headerOffset = 1

                var bitmap = BitmapFactory.decodeByteArray(fullMsg, headerOffset, fullMsg.size-headerOffset)// make image into 2d img from byte map
                val imgBytes: Int = fullMsg.size-headerOffset
                var attempts = 1
                while((bitmap == null || imgBytes < MIN_IMGTYPE1_BYTES) && msgAwaitCanBlockThread){
                    ioSafeSleep(READ_AFTER_SEND_DELAY)
                    clearInboundBuff()
                    if(msgAwaitCanBlockThread){
                        sendMessage(IMG_SECTION_REQUEST_MSG, debugOutput = false) // request and receive data for the next section of the img
                        val imgSectionBytesMsg = awaitServerMsg(debugOutput = false)
                        attempts++
                        Log.d(TCP_TAG, "Client - Receive attempt $attempts, got ${imgSectionBytesMsg.size-headerOffset} bytes")
                        if(imgSectionBytesMsg.isEmpty() || imgSectionBytesMsg[0] != IMG_SECTION_INDICATOR_BYTE)
                            continue
                        Log.d(TCP_TAG, "Attempting to make bitmap")
                        bitmap = BitmapFactory.decodeByteArray(imgSectionBytesMsg, headerOffset, imgSectionBytesMsg.size-headerOffset) // make image into 2d img from byte map, exclude the 1 byte header
                    }
                }
                Log.d(TCP_TAG, "Client - Received a valid img in $attempts attempt(s)")
                handleNewImage(bitmap) // handle img
                sendMessage(IMG_VALID_MSG) // request another image from the server once this one is handled
                awaitServerMsg() // receive the confirmation message
                //queueMessage(IMG_REQUEST_MSG)
            }
        }
        else{
            Log.i(TCP_TAG, "Client - Received empty message")
        }
    }


    private fun ioSafeSleep(msec: Long){
        val startTime = getTimeMillis()
        val endTime = startTime + msec
        while(getTimeMillis() < endTime) {}
    }
}