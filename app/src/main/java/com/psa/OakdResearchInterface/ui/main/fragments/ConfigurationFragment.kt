package com.psa.OakdResearchInterface.ui.main.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.psa.OakdResearchInterface.R
import com.psa.OakdResearchInterface.data.main.ListDataKeeper
import com.psa.OakdResearchInterface.databinding.FragmentConfigurationBinding
import com.psa.OakdResearchInterface.databinding.FragmentMainBinding
import com.psa.OakdResearchInterface.ui.main.view_models.MasterViewModel



class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MasterViewModel by activityViewModels() // grab the already instantiated view model and create a reference for it here

    // View references
    private var _farmCode: EditText? = null
    private val farmCode get() = _farmCode!!

    // Spinner references
    private var _cashCrop: Spinner? = null
    private val cashCrop get() = _cashCrop!!
    private var _coverCrop: Spinner? = null
    private val coverCrop get() = _coverCrop!!
    private var _weatherCond: Spinner? = null
    private val weatherCond get() = _weatherCond!!


    // debugging view references
    private var _farmCodeDebug: TextView? = null
    private val farmCodeDebug get() = _farmCodeDebug!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        val root = binding.root

        // Setup references and bind actions for this fragment's elements
        _farmCode = binding.editTextFarmCode
        // sets a listener to execute the lambda when text changes
        farmCode.doAfterTextChanged {
            mainViewModel.setFarmCode(it.toString()) // "it" being the given parameter name for the text object (of type mutable Editable)
        }
        mainViewModel.farmCodeUpdateList.add { Log.d("PSA_RSI_DEBUGGING", mainViewModel.farmCode.value.toString()) }

        _cashCrop = binding.cashCropSpinner
        initSpinnerItems(cashCrop, R.array.cash_crops)
        _coverCrop = binding.coverCropSpinner
        initSpinnerItems(coverCrop, R.array.cover_crops)
        _weatherCond = binding.weatherSpinner
        initSpinnerItems(weatherCond, R.array.weather_conditions)

        // setup references and bind actions for this fragment's debugging elements
        /*
        _farmCodeDebug = binding.farmCodeDebugDisplayView
        mainViewModel.farmCodeUpdateList.add { farmCodeDebug.text = mainViewModel.farmCode.value } // execute this code when the farm code updates
        */

        return root
    }


    private fun initSpinnerItems(spinner: Spinner, itemsXMLResource: Int){
        ArrayAdapter.createFromResource( // pass in the context, items, and layout
            this.requireContext(),
            itemsXMLResource,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

}