package com.psa.OakdResearchInterface.ui.main.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.psa.OakdResearchInterface.R
import com.psa.OakdResearchInterface.databinding.FragmentConfigurationBinding
import com.psa.OakdResearchInterface.databinding.FragmentMainBinding
import com.psa.OakdResearchInterface.ui.main.view_models.MasterViewModel



class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MasterViewModel by activityViewModels() // grab the already instantiated view model and create a reference for it here

    // view references
    private var _farmCode: EditText? = null
    private val farmCode get() = _farmCode!!

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

        // setup references and bind actions for this fragment's elements
        _farmCode = binding.editTextFarmCode
        // sets a listener to execute the lambda when text changes
        farmCode.doAfterTextChanged {
            mainViewModel.setFarmCode(it.toString()) // "it" being the given parameter name for the text object (of type mutable Editable)
        }

        // setup references and bind actions for this fragment's debugging elements
        _farmCodeDebug = binding.farmCodeDebugDisplayView
        mainViewModel.farmCodeUpdateList.add { farmCodeDebug.text = mainViewModel.farmCode.value } // execute this code when the farm code updates
        mainViewModel.farmCodeUpdateList.add { Log.d("PSA_RSI_DEBUGGING", mainViewModel.farmCode.value.toString()) }


        return root
    }



}