package com.psa.OakdResearchInterface.ui.main.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MasterViewModel : ViewModel() { // holds data that can be shared between the main activity and all fragments
    // The client's message handler, routes/queues/stores all inbound and outbound messages as needed
    //val messageHandler: TCPMessageHandler = TCPMessageHandler()


    // Lists of expressions to run under given circumstances
    val farmCodeUpdateList: MutableList<()->Unit> = mutableListOf()
    val plotNumberUpdateList: MutableList<()->Unit> = mutableListOf()
    val seasonStageUpdateList: MutableList<()->Unit> = mutableListOf()
    val cashCropUpdateList: MutableList<()->Unit> = mutableListOf()
    val coverCropUpdateList: MutableList<()->Unit> = mutableListOf()
    val weatherCondUpdateList: MutableList<()->Unit> = mutableListOf()
    val startSessionButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val stopSessionButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val selectAllButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val uploadButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val deleteButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
   // val sessionDataUpdateList: MutableList<()->Unit> = mutableListOf()



    // Function to execute expressions from a passed in list
    private fun executeExpressions(expressionList: MutableList<()->Unit>){
        for (expression in expressionList) { // iterate through each expression
            expression.invoke() // execute it
        }
    }

    
    // Configuration Triggers
    private val mutableFarmCode = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val farmCode: LiveData<String> get() = mutableFarmCode // variable that is updated once safe to be, the variable that data is retrieved from
    fun setFarmCode(newValue: String){ // public setter for the private mutable variable
        mutableFarmCode.value = newValue
        executeExpressions(farmCodeUpdateList)
    }
    private val mutablePlotNumber = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val plotNumber: LiveData<String> get() = mutablePlotNumber // variable that is updated once safe to be, the variable that data is retrieved from
    fun setPlotNumber(newValue: String){ // public setter for the private mutable variable
        mutablePlotNumber.value = newValue
        executeExpressions(plotNumberUpdateList)
    }
    private val mutableSeasonStage = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val seasonStage: LiveData<String> get() = mutableSeasonStage // variable that is updated once safe to be, the variable that data is retrieved from
    fun setSeasonStage(newValue: String){ // public setter for the private mutable variable
        mutableSeasonStage.value = newValue
        executeExpressions(seasonStageUpdateList)
    }
    private val mutableCashCrop = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val cashCrop: LiveData<String> get() = mutableCashCrop // variable that is updated once safe to be, the variable that data is retrieved from
    fun setCashCrop(newValue: String){ // public setter for the private mutable variable
        mutableCashCrop.value = newValue
        executeExpressions(cashCropUpdateList)
    }
    private val mutableCoverCrop = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val coverCrop: LiveData<String> get() = mutableCoverCrop // variable that is updated once safe to be, the variable that data is retrieved from
    fun setCoverCrop(newValue: String){ // public setter for the private mutable variable
        mutableCoverCrop.value = newValue
        executeExpressions(coverCropUpdateList)
    }
    private val mutableWeatherCond = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val weatherCond: LiveData<String> get() = mutableWeatherCond // variable that is updated once safe to be, the variable that data is retrieved from
    fun setWeatherCond(newValue: String){ // public setter for the private mutable variable
        mutableWeatherCond.value = newValue
        executeExpressions(weatherCondUpdateList)
    }
    
    // Data Collection Triggers
    private val mutableStartSessionButtonState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val startSessionButtonState: LiveData<String> get() = mutableStartSessionButtonState // variable that is updated once safe to be, the variable that data is retrieved from
    fun setStartSessionButtonState(newValue: String){ // public setter for the private mutable variable
        mutableStartSessionButtonState.value = newValue
        executeExpressions(startSessionButtonStateUpdateList)
    }
    private val mutableStopSessionButtonState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val stopSessionButtonState: LiveData<String> get() = mutableStopSessionButtonState // variable that is updated once safe to be, the variable that data is retrieved from
    fun setStopSessionButtonState(newValue: String){ // public setter for the private mutable variable
        mutableStopSessionButtonState.value = newValue
        executeExpressions(stopSessionButtonStateUpdateList)
    }
    
    // Data Review Triggers
    private val mutableSelectAllButtonState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val selectAllButtonState: LiveData<String> get() = mutableSelectAllButtonState // variable that is updated once safe to be, the variable that data is retrieved from
    fun setSelectAllButtonState(newValue: String){ // public setter for the private mutable variable
        mutableSelectAllButtonState.value = newValue
        executeExpressions(selectAllButtonStateUpdateList)
    }
    private val mutableUploadButtonState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val uploadButtonState: LiveData<String> get() = mutableUploadButtonState // variable that is updated once safe to be, the variable that data is retrieved from
    fun setUploadButtonState(newValue: String){ // public setter for the private mutable variable
        mutableUploadButtonState.value = newValue
        executeExpressions(uploadButtonStateUpdateList)
    }
    private val mutableDeleteButtonState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val deleteButtonState: LiveData<String> get() = mutableDeleteButtonState // variable that is updated once safe to be, the variable that data is retrieved from
    fun setDeleteButtonState(newValue: String){ // public setter for the private mutable variable
        mutableDeleteButtonState.value = newValue
        executeExpressions(deleteButtonStateUpdateList)
    }

    /*private val mutableSessionDataList = MutableLiveData<MutableList<SessionData>>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val sessionDataList: LiveData<MutableList<SessionData>> get() = mutableSessionDataList // variable that is updated once safe to be, the variable that data is retrieved from
    val updateSessionDataDisplay: (MutableList<SessionData>)->Unit = {
        mutableSessionDataList.value=it
        executeExpressions(sessionDataUpdateList)
    }*/
}