package com.psa.oakdresearchinterface.data.collection

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.psa.oakdresearchinterface.data.PAUSE_COLLECT_MSG
import com.psa.oakdresearchinterface.data.START_COLLECT_MSG
import com.psa.oakdresearchinterface.data.STOP_COLLECT_MSG
import com.psa.oakdresearchinterface.data.UNPAUSE_COLLECT_MSG
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class OakDController(private val handleNewImage: (Bitmap)->Unit) : CameraController {
    private lateinit var tcpClient: TCPClient

    init {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            //Background work here
            tcpClient = TCPClient(handleNewImage)
            tcpClient.run()

            handler.post { // UI work here

            }
        }
    }

    override fun startCollection() {
        tcpClient.queueMessage(START_COLLECT_MSG)
    }


    override fun pauseCollection() {
        tcpClient.queueMessage(PAUSE_COLLECT_MSG)
    }

    override fun unpauseCollection() {
        tcpClient.queueMessage(UNPAUSE_COLLECT_MSG)
    }

    override fun stopCollection() {
        tcpClient.queueMessage(STOP_COLLECT_MSG)
    }
}