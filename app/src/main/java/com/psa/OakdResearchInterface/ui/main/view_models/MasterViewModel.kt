package com.psa.OakdResearchInterface.ui.main.view_models

import android.content.ClipData.Item
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MasterViewModel : ViewModel() { // holds data that can be shared between the main activity and all fragments
    // Lists of expressions to run under given circumstances
    val farmCodeUpdateList: MutableList<()->Unit> = mutableListOf()
    // Function to execute expressions from a passed in list
    private fun executeExpressions(expressionList: MutableList<()->Unit>){
        for (expression in expressionList) { // iterate through each expression
            expression.invoke() // execute it
        }
    }

    // Configuration variables
    private val mutableFarmCode = MutableLiveData<String>() // value safe to be set asynchronously (private to prevent data retrieval from it)
    val farmCode: LiveData<String> get() = mutableFarmCode // variable that is updated once safe to be, the variable that data is retrieved from
    fun setFarmCode(newCode: String){ // public setter for the private mutable variable
        mutableFarmCode.value = newCode
        executeExpressions(farmCodeUpdateList)
    }

}