package com.psa.oakdresearchinterface.data.main.depricated

import android.util.Log
import com.psa.oakdresearchinterface.data.TCP_TAG
import com.psa.oakdresearchinterface.ui.main.view_models.MasterViewModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.concurrent.thread


@OptIn(InternalAPI::class)
class Ktor_TCPClient(private val targetHostname: String, private val targetPort:Int) {

    init {
        Log.d("PSA_RSI_DEBUGGING", "Manager object initialized with target port $targetPort of $targetHostname")
    }


    var clientOperationRunning: Boolean = false
    fun startClientOperation(masterViewModel: MasterViewModel){
        startClientOperationForGiven(targetHostname, targetPort)
    }
    fun startTestClientOperation(port: Int){
        startClientOperationForGiven(getLocalDeviceIP(), port)
    }
    private fun startClientOperationForGiven(hostname: String, port: Int){
        requestClientStop()

        thread(start = true, name = "CLIENT_MONITOR"){
            runBlocking {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val socket = aSocket(selectorManager).tcp().connect(hostname, port)
                Log.d(TCP_TAG, "Client opened server connection")
                val receiveChannel = socket.openReadChannel()
                Log.d(TCP_TAG, "Client receive channel initialized")
                val sendChannel: ByteWriteChannel = socket.openWriteChannel(autoFlush = true)
                Log.d(TCP_TAG, "Client send channel initialized")

                launch(Dispatchers.IO) {
                    Log.d(TCP_TAG, "Client handler thread launched")
                    while (clientOperationRunning) {
                        Log.d(TCP_TAG, "Client ready to receive message")
                        val greeting = receiveChannel.readUTF8Line()
                        Log.d(TCP_TAG, "Client recieved message")
                        if (greeting != null) {
                            Log.i(TCP_TAG, "Client Connection Established. Server's Greeting: $greeting")
                        } else {
                            Log.i(TCP_TAG, "Server closed a connection")
                            socket.close()
                            selectorManager.close()
                            requestClientStop()
                            //exitProcess(0)
                        }
                    }
                }

                clientOperationRunning = true
                // execute actions once connected to the client
                while (clientOperationRunning) {
                    val myMessage = readln()
                    sendChannel.writeStringUtf8("$myMessage\n")
                }
            }
        }

    }
    fun requestClientStop(){
        Log.d(TCP_TAG, "Client stop requested")
        clientOperationRunning = false
    }


    private var testServerRunning: Boolean = true
    fun startPartnerTestServer(serverPort: Int){
        thread(start = true, name = "TEST_SERVER") {
            runBlocking { // blocking thread
                val serverSocket = aSocket(SelectorManager(Dispatchers.IO)).tcp()
                    .bind(getLocalDeviceIP(), serverPort)
                Log.d(TCP_TAG, "Test Server Started. Listening at: ${serverSocket.localAddress}")

                testServerRunning = true
                while (testServerRunning) { // only runs the server for as long as it's allowed
                    val socket = serverSocket.accept()
                    Log.d(TCP_TAG, "Test Server accepted socket: $socket")

                    launch { // parallel thread
                        Log.d(TCP_TAG, "Server handler thread launched")
                        val receiveChannel = socket.openReadChannel()
                        Log.d(TCP_TAG, "Server receive channel initialized")
                        val sendChannel = socket.openWriteChannel(autoFlush = true)
                        Log.d(TCP_TAG, "Server send channel initialized")
                        sendChannel.writeStringUtf8("Please enter your name\n")
                        Log.d(TCP_TAG, "Server sent greeting")
                        try {
                            while (testServerRunning and false) {
                                Log.d(TCP_TAG, "Server ready to receive message")
                                runBlocking {
                                    Log.d(TCP_TAG, "Server waiting for client response")
                                    delay(2000)
                                }
                                val name = receiveChannel.readUTF8Line()
                                Log.d(TCP_TAG, "Server received message.")
                                Log.d(TCP_TAG, "Server received message: $name")
                                sendChannel.writeStringUtf8("Hello, $name!\n")
                            }
                            socket.close()
                        } catch (e: Throwable) {
                            socket.close()
                        }
                    }
                }
            }
        }
    }
    fun requestTestServerStop(){
        Log.d(TCP_TAG, "Test server stop requested")
        testServerRunning = false
    }


    fun getLocalDeviceIP(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }

        return "NULL" // if getting the address fails
    }
}