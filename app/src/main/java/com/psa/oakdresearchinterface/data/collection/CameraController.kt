package com.psa.oakdresearchinterface.data.collection

import android.media.Image


interface CameraController {
    fun startCollection()
    fun pauseCollection()
    fun unpauseCollection()
    fun stopCollection()

}