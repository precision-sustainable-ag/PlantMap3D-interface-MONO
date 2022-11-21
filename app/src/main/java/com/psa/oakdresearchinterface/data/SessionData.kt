package com.psa.oakdresearchinterface.data

import android.content.Context
import android.util.Log
import com.psa.oakdresearchinterface.data.storage.SessionMetadata
import java.io.File
import java.io.IOException


class SessionData (context: Context, targetSessionID: String, sessionMetadata: SessionMetadata, val isExistingData: Boolean = false) {
    private var _sessionID: String? = null
    val sessionID get() = _sessionID!!
    private var _dataSelected: Boolean = false
    val isSelected get() = _dataSelected
    private var _metadata: SessionMetadata
    val metadata get() = _metadata

    private lateinit var sessionDataFile: File
    private var fileInitialized: Boolean = false


    init {
        _metadata = sessionMetadata
        if(!isExistingData) {
            createDataDir(context, targetSessionID)
        }
        else{
            _sessionID = targetSessionID
        }
        metadata.sessionID = sessionID
    }



    private fun createDataDir(context: Context, sessionID: String){
        val dataRoot = File(context.filesDir, DATA_DIRECTORY) // the location all session data is stored in
        if(!dataRoot.exists()){
            dataRoot.mkdirs()
        }
        var subDirRoot: File // the sub directory this session's data will be stored in

        Log.i(DATA_TAG, "Attempting to create directory for session: $sessionID")

        // Log.d(DATA_TAG, "Internal Storage Dir: ${context.filesDir}")
        // Log.d(DATA_TAG, "Data Directory: $DATA_DIRECTORY")

        var duplicateInc = 0
        do {
            _sessionID = sessionID
            if (duplicateInc > 0){
                _sessionID += "(${duplicateInc})"
            }
            Log.d(DATA_TAG, "Attempted save directory: ${this.sessionID}")
            subDirRoot = File( dataRoot, this.sessionID)

            duplicateInc++ // increment the number of existing directories that share this session ID
        } while (subDirRoot.exists())
        subDirRoot.mkdirs() // once reached a directory name that doesn't already exist, make it

        Log.d(DATA_TAG,"${duplicateInc-1} existing directories share the session ID: $sessionID")
        Log.i(DATA_TAG, "Session directory created with name: ${this.sessionID}")

        sessionDataFile = File(subDirRoot, "${this.sessionID}$DATA_FILE_EXTENSION")
        try {
            if(sessionDataFile.createNewFile()){
                fileInitialized = true
                Log.i(DATA_TAG, "Session data file created (${this.sessionID}$DATA_FILE_EXTENSION) in session directory")
            }
            else{
                throw IOException("File ${sessionDataFile.absoluteFile} Already Exists at Directory ${sessionDataFile.absolutePath}")
            }
            // if unable to create a new file at location, could load the file
            // (the create new file method returns true if could create a new file)
        } catch (e: IOException) {
            Log.e(DATA_TAG, "File Creation Error - $e")
        }
    }


    fun setSelected(dataSelected: Boolean){
        this._dataSelected = dataSelected
        if(dataSelected)
            Log.d(UI_TAG, "Data directory selected: $sessionID")
        else
            Log.d(UI_TAG, "Data directory deselected: $sessionID")
    }

    fun formatForUpload(): MutableList<Unit>{
        return mutableListOf()// TODO: Load that data into memory, then return it (formatted for our Azure cloud)
    }
    fun deleteSelf(){
        Log.d(UI_TAG, "Attempting to delete data directory: $sessionID")
        Log.d(DATA_TAG, "Attempting to delete data directory: $sessionID")
        return // TODO: Make data delete itself
    }
}