package com.psa.oakdresearchinterface.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SessionMetadata::class], version=3)
abstract class SessionDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}