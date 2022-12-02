package com.psa.oakdresearchinterface.ui.main.view_models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MasterViewModel : ViewModel() { // holds data that can be shared between the main activity and all fragments


    // Lists of expressions to run under given circumstances
    val farmCodeUpdateList: MutableList<()->Unit> = mutableListOf()
    val plotNumberUpdateList: MutableList<()->Unit> = mutableListOf()
    val seasonStageUpdateList: MutableList<()->Unit> = mutableListOf()
    val cashCropUpdateList: MutableList<()->Unit> = mutableListOf()
    val coverCropUpdateList: MutableList<()->Unit> = mutableListOf()
    val weatherCondUpdateList: MutableList<()->Unit> = mutableListOf()
    val sessionRunStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val selectAllButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val uploadButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    val deleteButtonStateUpdateList: MutableList<()->Unit> = mutableListOf()
    //val handleNewImageUpdateList: MutableList<()->Unit> = mutableListOf()
   // val sessionDataUpdateList: MutableList<()->Unit> = mutableListOf()



    // Function to execute expressions from a passed in list
    private fun executeExpressions(expressionList: MutableList<()->Unit>, arg: Unit?=null){
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
    private val mutablePlotNumber = MutableLiveData<Int>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val plotNumber: LiveData<Int> get() = mutablePlotNumber // variable that is updated once safe to be, the variable that data is retrieved from
    fun setPlotNumber(newValue: String){ // public setter for the private mutable variable
        mutablePlotNumber.value = newValue.toIntOrNull()
        if(mutablePlotNumber.value == null)
            mutablePlotNumber.value = -1
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
    private val mutableSessionRunState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val sessionRunState: LiveData<String> get() = mutableSessionRunState // variable that is updated once safe to be, the variable that data is retrieved from
    fun setSessionRunState(newValue: String){ // public setter for the private mutable variable
        mutableLastSessionRunState.value = mutableSessionRunState.value
        mutableSessionRunState.value = newValue
        executeExpressions(sessionRunStateUpdateList)
    }
    private val mutableLastSessionRunState = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val lastSessionRunState: LiveData<String> get() = mutableLastSessionRunState // variable that is updated once safe to be, the variable that data is retrieved from


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

    //private val mutableRecentImageInput = MutableLiveData<Bitmap>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    //val recentImageInput: LiveData<Bitmap> get() = mutableRecentImageInput // variable that is updated once safe to be, the variable that data is retrieved from
    var setImageDisplayFunc: ((Bitmap)->Unit)? = null
    fun handleNewImage (newImg: Bitmap) {
        setImageDisplayFunc!!(newImg)
    }
}