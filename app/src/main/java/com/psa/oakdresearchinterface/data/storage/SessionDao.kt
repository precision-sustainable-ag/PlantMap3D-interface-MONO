package com.psa.oakdresearchinterface.data.storage

import androidx.room.*


@Dao
interface SessionDao {
    @Query("SELECT * FROM SessionMetadata")
    fun getAll(): List<SessionMetadata>

    @Query("SELECT * from SessionMetadata WHERE sessionID = :sessionID")
    fun getByID(sessionID: String): SessionMetadata?

    @Insert
    fun insertMetadata(metadata: SessionMetadata)

    @Delete
    fun deleteMetadata(metadata: SessionMetadata)

    @Update
    fun updateMetadata(metadata: SessionMetadata)
}