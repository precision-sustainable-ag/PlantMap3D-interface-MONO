package com.psa.OakdResearchInterface.data

import android.util.Log

class SessionDataManager (val updateUIFunc: (List<SessionData>) -> Unit, val  uploadDataFunc: (SessionData) -> Unit) {
    val sessionDataList: MutableList<SessionData> = mutableListOf()

    init {
        updateDataDisplay() // ensure display is up to date and eveything is properly initialized
    }

    fun addData(sessionData: SessionData){ // adds the session data to the list and updates the UI to represent that
        insertDataIntoList(sessionData)
        updateDataDisplay()
    }
    fun addData(sessionData: MutableList<SessionData>){ // adds a list session data to the list and updates the UI to represent that
        for(data in sessionData)
            insertDataIntoList(data)
        updateDataDisplay()
    }
    private fun insertDataIntoList(sessionData: SessionData){ // adds session data into the list while maintaining alphabetical order
        if(sessionDataList.isEmpty()) {
            sessionDataList.add(sessionData)
        }
        else {
            for(i in 0 until  sessionDataList.size){
                if(sessionData.sessionSubDir < sessionDataList[i].sessionSubDir){ // if data's directory name is alphabetically less than the current index
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

    }
    fun uploadSelectedData() {
        val selectedData = getSelectedData()
        for(data in selectedData)
            uploadDataFunc(data)
        if(selectedData.size <= 0)
            Log.d(UI_CLEAN_TAG, "Data upload attempted, no data to upload")
    }

    fun updateDataDisplay(){
        updateUIFunc(sessionDataList)
    }
}