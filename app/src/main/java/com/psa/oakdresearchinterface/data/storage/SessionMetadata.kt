package com.psa.oakdresearchinterface.data.storage

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class SessionMetadata (
    var sessionID: String = "ID",
    var farmCode: String = "CODE",
    var plotNumber: Int = 0,
    var seasonTiming: String = "TIMING",
    var coverCrop: String = "",
    var cashCrop: String = "",
    var weatherCond: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var sessionNumID: Int = 0
}