package com.psa.oakdresearchinterface.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.psa.oakdresearchinterface.data.storage.SessionDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SessionDataManager (private val context: Context, private val updateUIFunc: (List<SessionData>) -> Unit) {
    val sessionDataList: MutableList<SessionData> = mutableListOf()

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private val database = Room.databaseBuilder(context, SessionDatabase::class.java, DATABASE_NAME).build()
    private val sessionDao = database.sessionDao()


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
            Log.d(UI_CLEAN_TAG, "Attempting data deletion for ${selectedData.size} item(s)")
            for (data in selectedData)
                data.deleteSelf()
        }
        updateDataUI()
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