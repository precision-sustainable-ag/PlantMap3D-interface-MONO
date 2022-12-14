package com.psa.oakdresearchinterface.ui.main.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.room.Room
import com.psa.oakdresearchinterface.R
import com.psa.oakdresearchinterface.data.DATABASE_NAME
import com.psa.oakdresearchinterface.data.DATA_TAG
import com.psa.oakdresearchinterface.databinding.FragmentConfigurationBinding
import com.psa.oakdresearchinterface.data.UI_TAG
import com.psa.oakdresearchinterface.data.storage.SessionDatabase
import com.psa.oakdresearchinterface.ui.main.view_models.MasterViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MasterViewModel by activityViewModels() // grab the already instantiated view model and create a reference for it here

    // View references
    private var _farmCode: EditText? = null
    private val farmCode get() = _farmCode!!
    private var _plotNumber: EditText? = null
    private val plotNumber get() = _plotNumber!!
    private var _seasonStage: EditText? = null
    private val seasonStage get() = _seasonStage!!

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

    // Flags
    private var updateListInited = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.farmCodeUpdateList.add { Log.d(UI_TAG, "Farm Code value updated to: ${mainViewModel.farmCode.value.toString()}") }
        mainViewModel.plotNumberUpdateList.add { Log.d(UI_TAG, "Plot Number value updated to: ${mainViewModel.plotNumber.value.toString()}") }
        mainViewModel.seasonStageUpdateList.add { Log.d(UI_TAG, "Season Stage value updated to: ${mainViewModel.seasonStage.value.toString()}") }
        mainViewModel.cashCropUpdateList.add { Log.d(UI_TAG, "Cash Crop value updated to: ${mainViewModel.cashCrop.value.toString()}") }
        mainViewModel.coverCropUpdateList.add { Log.d(UI_TAG, "Cover Crop value updated to: ${mainViewModel.coverCrop.value.toString()}") }
        mainViewModel.weatherCondUpdateList.add { Log.d(UI_TAG, "Weather Conditions value updated to: ${mainViewModel.weatherCond.value.toString()}") }
    }

    @SuppressLint("ClickableViewAccessibility")
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

        _plotNumber = binding.editTextPlotCode
        // sets a listener to execute the lambda when text changes
        plotNumber.doAfterTextChanged {
            mainViewModel.setPlotNumber(it.toString()) // "it" being the given parameter name for the text object (of type mutable Editable)
        }

        _seasonStage = binding.editTextSeasonTiming
        // sets a listener to execute the lambda when text changes
        seasonStage.doAfterTextChanged {
            mainViewModel.setSeasonStage(it.toString()) // "it" being the given parameter name for the text object (of type mutable Editable)
        }

        _cashCrop = binding.cashCropSpinner
        initSpinnerItems(cashCrop, R.array.cash_crops)
        cashCrop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mainViewModel.setCashCrop("")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mainViewModel.setCashCrop(cashCrop.selectedItem.toString())
            }
        }

        _coverCrop = binding.coverCropSpinner
        initSpinnerItems(coverCrop, R.array.cover_crops)
        coverCrop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mainViewModel.setCoverCrop("")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mainViewModel.setCoverCrop(coverCrop.selectedItem.toString())
            }
        }

        _weatherCond = binding.weatherSpinner
        initSpinnerItems(weatherCond, R.array.weather_conditions)
        weatherCond.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mainViewModel.setWeatherCond("")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mainViewModel.setWeatherCond(weatherCond.selectedItem.toString())
            }
        }


        // Load up the database temporarily to init the config field values to the most recently used values
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val database = Room.databaseBuilder(requireContext(), SessionDatabase::class.java, DATABASE_NAME).build()
            val metadataList = database.sessionDao().getAll()
            Log.d(DATA_TAG, "Metadata entries found: ${metadataList.size}")
            if(metadataList.isNotEmpty()){
                handler.post{
                    val recentMetadata = metadataList.last()
                    mainViewModel.setFarmCode(recentMetadata.farmCode)
                    farmCode.setText(recentMetadata.farmCode)
                    mainViewModel.setPlotNumber(recentMetadata.plotNumber.toString())
                    plotNumber.setText(recentMetadata.plotNumber.toString())
                    mainViewModel.setSeasonStage(recentMetadata.seasonTiming)
                    seasonStage.setText(recentMetadata.seasonTiming)
                    mainViewModel.setCashCrop(recentMetadata.cashCrop)
                    mainViewModel.setCoverCrop(recentMetadata.coverCrop)
                    mainViewModel.setWeatherCond(recentMetadata.weatherCond)
                }
            }
        }


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
    /*private fun getSpinnerPosOfItem(string: String, itemsXMLResource: Int): Int{
        val adapter = ArrayAdapter.createFromResource( // pass in the context, items, and layout
            this.requireContext(),
            itemsXMLResource,
            android.R.layout.simple_spinner_item
        )
        for(i in 0 until adapter.count){
            if(string == adapter.getItem(i)!!)
                return i
        }
        return 0
    }*/
}
