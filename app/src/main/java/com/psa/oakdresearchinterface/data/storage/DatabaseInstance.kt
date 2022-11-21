package com.psa.oakdresearchinterface.data.storage

import android.content.Context
import androidx.room.Room



object DatabaseInstance {

    private var databaseInstance: SessionDatabase? = null

    fun getInstance(context: Context): SessionDatabase {

        return databaseInstance ?: run {
            databaseInstance = Room.databaseBuilder(
                context,
                SessionDatabase::class.java, "weedsimagerepo"
            )
                .fallbackToDestructiveMigration()
                .build()
            databaseInstance!!
        }
    }


}