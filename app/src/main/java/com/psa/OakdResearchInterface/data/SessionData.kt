package com.psa.OakdResearchInterface.data

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException


class SessionData (context: Context, sessionID: String, attemptDataLoad: Boolean = false) {
    private var _sessionSubDir: String? = null
    val sessionSubDir get() = _sessionSubDir!!
    private var _dataSelected: Boolean = false
    val isSelected get() = _dataSelected

    private lateinit var sessionDataFile: File
    private var fileInitialized: Boolean = false


    init {
        if(attemptDataLoad) {
            loadDataFromID(context, sessionID)
        }
        else {
            createDataDir(context, sessionID)
        }
    }


    private fun loadDataFromID(context: Context, sessionID: String) {
        _sessionSubDir = sessionID
        return// TODO: Fill in with data parsing and initialization once the data is known
    }

    private fun createDataDir(context: Context, sessionID: String){
        var dataRoot = File(context.filesDir, DATA_DIRECTORY) // the location all session data is stored in
        if(!dataRoot.exists()){
            dataRoot.mkdirs()
        }
        var subDirRoot: File // the sub directory this session's data will be stored in

        Log.i(DATA_TAG, "Attempting to create directory for session: $sessionID")

        // Log.d(DATA_TAG, "Internal Storage Dir: ${context.filesDir}")
        // Log.d(DATA_TAG, "Data Directory: $DATA_DIRECTORY")

        var duplicateInc = 0
        do {
            _sessionSubDir = sessionID
            if (duplicateInc > 0){
                _sessionSubDir += "(${duplicateInc})"
            }
            Log.d(DATA_TAG, "Attempted save directory: $sessionSubDir")
            subDirRoot = File( dataRoot, sessionSubDir)

            duplicateInc++ // increment the number of existing directories that share this session ID
        } while (subDirRoot.exists())
        subDirRoot.mkdirs() // once reached a directory name that doesn't already exist, make it

        Log.d(DATA_TAG,"${duplicateInc-1} existing directories share the session ID: $sessionID")
        Log.i(DATA_TAG, "Session directory created with name: $sessionSubDir")

        sessionDataFile = File(subDirRoot, "$sessionSubDir$DATA_FILE_EXTENSION")
        try {
            if(sessionDataFile.createNewFile()){
                fileInitialized = true
                Log.i(DATA_TAG, "Session data file created ($sessionSubDir$DATA_FILE_EXTENSION) in session directory")
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
            Log.d(UI_TAG, "Data directory selected: $sessionSubDir")
        else
            Log.d(UI_TAG, "Data directory deselected: $sessionSubDir")
    }

    fun formatForUpload(): MutableList<Unit>{
        return mutableListOf()// TODO: Load that data into memory, then return it (formatted for our Azure cloud)
    }
    fun deleteSelf(){
        Log.d(UI_TAG, "Attempting to delete data directory: $sessionSubDir")
        Log.d(DATA_TAG, "Attempting to delete data directory: $sessionSubDir")
        return // TODO: Make data delete itself
    }
}