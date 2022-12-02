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

        executor.execute {
            //Background work here
            tcpClient = TCPClient(handleNewImage)
            tcpClient.run()
        }
    }

    override fun startCollection() {
        tcpClient.queueMessage(START_COLLECT_MSG)
    }


    override fun pauseCollection() {
        tcpClient.interruptMessageWait()
        tcpClient.clearInboundBuff()
        tcpClient.queueMessage(PAUSE_COLLECT_MSG)
    }

    override fun unpauseCollection() {
        tcpClient.queueMessage(UNPAUSE_COLLECT_MSG)
    }

    override fun stopCollection() {
        tcpClient.interruptMessageWait()
        tcpClient.clearInboundBuff()
        tcpClient.queueMessage(STOP_COLLECT_MSG)
    }
}