package com.psa.oakdresearchinterface.data

import android.content.Context
import android.os.FileUtils
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.psa.oakdresearchinterface.data.storage.SessionDatabase
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SessionDataManager (private val context: Context, private val updateUIFunc: (List<SessionData>) -> Unit) {
    val sessionDataList: MutableList<SessionData> = mutableListOf()

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private val database = Room.databaseBuilder(context, SessionDatabase::class.java, DATABASE_NAME).build()
    private val sessionDao = database.sessionDao()

    init {
        loadSessionDataList()
    }

    private fun loadSessionDataList(){
        sessionDataList.clear()
        val imgDirs = getExistingImgDirs()
        executor.execute{
            Log.d(DATA_TAG, sessionDao.getAll().size.toString())
            for(metadata in sessionDao.getAll()){
                if(metadata.sessionID in imgDirs){ // only consider metadata valid if it has an associated img directory
                    sessionDataList.add(SessionData(context, metadata.sessionID, metadata, isExistingData = true))
                }
                else{ // if invalid, purge it from the database
                    sessionDao.deleteMetadata(metadata)
                }
            }
        }
    }
    private fun getExistingImgDirs(): MutableList<String> {
        val dataRoot = File(context.filesDir, DATA_DIRECTORY) // the location all session data is stored in
        if(!dataRoot.exists()){
            dataRoot.mkdirs()
        }
        val subDirs = dataRoot.listFiles()
        val subDirNames = mutableListOf<String>()
        for(dir in subDirs!!){
            subDirNames.add(dir.name)
            Log.d(DATA_TAG, "Directory found: ${dir.name}")
        }
        return subDirNames
    }

    fun addData(sessionData: SessionData, updateUI: Boolean = true){ // adds the session data to the list and updates the UI to represent that
        executor.execute{ // run these operations on a different thread, especially as they might interact with the database
            insertData(sessionData)
            if(updateUI)
                 handler.post { updateDataUI() }
        }
    }
    fun addData(sessionData: MutableList<SessionData>, updateUI: Boolean = true){ // adds a list session data to the list and updates the UI to represent that
        executor.execute{ // run these operations on a different thread, especially as they might interact with the database
            for(data in sessionData)
                insertData(data)
            if(updateUI)
                 handler.post { updateDataUI() }
        }
    }
    private fun insertData(sessionData: SessionData){ // adds session data into the list while maintaining alphabetical order
        // if the metadata for this item doesn't already exist in the database
        if(!sessionData.isExistingData && sessionDao.getByID(sessionData.metadata.sessionID) == null){
            sessionDao.insertMetadata(sessionData.metadata) // add it
            Log.d(DATA_TAG, "Adding Metadata: ${sessionData.sessionID}")
        }

        if(sessionDataList.isEmpty()) {
            sessionDataList.add(sessionData)
        }
        else { // if data existing, insertion sort
            for(i in 0 until  sessionDataList.size){
                if(sessionData.sessionID < sessionDataList[i].sessionID){ // if data's directory name is alphabetically less than the current index
                    sessionDataList.add(i, sessionData) // insert it at that index
                    return // the function's job is over, end its call
                }
            }
            sessionDataList.add(sessionData) // if no item with lower alphabetical order found after full list traversal, add to end
        }
    }

    private fun getSelectedData(): MutableList<SessionData> {
        val selectedData: MutableList<SessionData> = mutableListOf()
        for(data in sessionDataList)
            if(data.isSelected)
                selectedData.add(data)
        return selectedData
    }
    fun deleteSelectedData() {
        val selectedData = getSelectedData()
        if(selectedData.size <= 0)
            Log.d(UI_CLEAN_TAG, "Data deletion attempted, no data to delete")
        else {
            executor.execute{ // do this in its own thread because it interacts with the database (and with the file system)
                Log.d(UI_CLEAN_TAG, "Attempting data deletion for ${selectedData.size} item(s)")
                val dataRoot = File(context.filesDir, DATA_DIRECTORY) // the location all session data is stored in
                for(data in selectedData){
                    val fileToDel = File(dataRoot, data.sessionID) // delete the data's storage sub-directory
                    if (fileToDel.exists()) {
                        try {
                            if(fileToDel.deleteRecursively()){
                                sessionDao.deleteMetadata(data.metadata) // then delete the associated metadata if successful
                                sessionDataList.remove(data) // and remove it from the session data list
                                Log.i(DATA_TAG, "Deleted directory & metadata for: ${fileToDel.name}")

                            }
                            else
                                Log.e(DATA_TAG, "ERROR - Unable to delete directory: ${fileToDel.name}")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    else
                        Log.e(DATA_TAG, "ERROR - Unable to find directory: ${fileToDel.name}")
                }
                handler.post { updateDataUI() } //update the UI in the same thread to make sure it updates after items are deleted
            }
        }
    }
    fun uploadSelectedData() {
        val selectedData = getSelectedData()
        for(data in selectedData)
            uploadData(data)
        if(selectedData.size <= 0)
            Log.d(UI_CLEAN_TAG, "Data upload attempted, no data to upload")
    }

    fun updateDataUI(){
        updateUIFunc(sessionDataList)
    }
    private fun uploadData(data: SessionData){
        val uploadReadyData: MutableList<Unit> = data.formatForUpload()
        // TODO: Upload data to our Azure cloud

        Log.d(UI_CLEAN_TAG, "Data upload started for data directory: ${data.sessionID}")
        Log.d(DATA_TAG, "Data upload started for data directory: ${data.sessionID}")
        Toast.makeText(context, "Data upload started for: ${data.sessionID}", Toast.LENGTH_SHORT).show()
    }
}