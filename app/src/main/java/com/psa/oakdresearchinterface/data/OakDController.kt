package com.psa.oakdresearchinterface.data

import android.media.Image

class OakDController(val handleNewImage: (Image)->Unit) : CameraController {
    private lateinit var tcpClient: TCPClient

    init {

    }

    override fun startCollection() {
        tcpClient.queueMessage(START_COLLECT_MSG)
    }


    override fun pauseCollection() {
        tcpClient.queueMessage(PAUSE_COLLECT_MSG)
    }

    override fun stopCollection() {
        tcpClient.queueMessage(STOP_COLLECT_MSG)
    }
}