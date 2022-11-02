package com.psa.OakdResearchInterface.ui.main.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.psa.OakdResearchInterface.R
import com.psa.OakdResearchInterface.data.*
import com.psa.OakdResearchInterface.databinding.FragmentReviewBinding
import com.psa.OakdResearchInterface.ui.main.view_models.MasterViewModel
import java.lang.Double.min
import java.util.zip.Inflater


class ReviewFragment : Fragment() {
    private val maxDataFilesPerRow = 5

    // These properties are only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!


    private val mainViewModel: MasterViewModel by activityViewModels() // grab the already instantiated view model and create a reference for it here

    // Button references
    private var _selectAllButton: ImageButton? = null
    private val selectAllButton get() = _selectAllButton!!
    private var _uploadButton: ImageButton? = null
    private val uploadButton get() = _uploadButton!!
    private var _deleteButton: ImageButton? = null
    private val deleteButton get() = _deleteButton!!
  //  private var _dataScrollView: ScrollView? = null
  //  private val dataScrollView get() = _dataScrollView!!
    private var _dataFileLayout: LinearLayout? = null
    private val dataFileLayout get() = _dataFileLayout!!

    private lateinit var sessionDataManager: SessionDataManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root = binding.root

        // Init button/view bindings here
        _selectAllButton = binding.selectAllButton
        selectAllButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                selectAllButton.setImageResource(R.drawable.select_all_button_clicked)
                Log.d(UI_TAG, "Select all button: Pressed")
                mainViewModel.setSelectAllButtonState(BUTTON_PRESSED)
            }
            else if (event.action == MotionEvent.ACTION_UP) {
                selectAllButton.setImageResource(R.drawable.select_all_button)
                Log.d(UI_TAG, "Select all button: Released")
                mainViewModel.setSelectAllButtonState(BUTTON_NOT_PRESSED)
            }
            false
        }
        mainViewModel.selectAllButtonStateUpdateList.add{
            if(mainViewModel.selectAllButtonState.value == BUTTON_PRESSED)
                selectAllData()
        }

        _uploadButton = binding.uploadButton
        uploadButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                uploadButton.setImageResource(R.drawable.upload_button_clicked)
                Log.d(UI_TAG, "Upload button: Pressed")
                mainViewModel.setUploadButtonState(BUTTON_PRESSED)
            }
            else if (event.action == MotionEvent.ACTION_UP) {
                uploadButton.setImageResource(R.drawable.upload_button)
                Log.d(UI_TAG, "Upload button: Released")
                mainViewModel.setUploadButtonState(BUTTON_NOT_PRESSED)
            }
            false
        }
        mainViewModel.uploadButtonStateUpdateList.add{
            if(mainViewModel.uploadButtonState.value == BUTTON_PRESSED)
                sessionDataManager.uploadSelectedData()
        }

        _deleteButton = binding.deleteButton
        deleteButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                deleteButton.setImageResource(R.drawable.delete_button_clicked)
                Log.d(UI_TAG, "Delete button: Pressed")
                mainViewModel.setDeleteButtonState(BUTTON_PRESSED)
            }
            else if (event.action == MotionEvent.ACTION_UP) {
                deleteButton.setImageResource(R.drawable.delete_button)
                Log.d(UI_TAG, "Delete button: Released")
                mainViewModel.setDeleteButtonState(BUTTON_NOT_PRESSED)
            }
            false
        }
        mainViewModel.deleteButtonStateUpdateList.add{
            if(mainViewModel.deleteButtonState.value == BUTTON_PRESSED)
                sessionDataManager.deleteSelectedData()
        }


        // Setup Session Data
       // _dataScrollView = binding.fileScrollView
        _dataFileLayout = binding.dataFileLinearLayout
        sessionDataManager = SessionDataManager(
            updateSessionDataDisplay,
            uploadSessionData
        )
        //val testSessionData = SessionData(applicationContext, "TEST_SESSION")
        //sessionDataManager.updateDataDisplay()
        val testDataList = mutableListOf<SessionData>()
        for(i in 0 until maxDataFilesPerRow+2){
            testDataList.add(SessionData(requireContext(), "TEST_SESSION($i)", attemptDataLoad = true))
        }
        sessionDataManager.addData(testDataList)



        return root
    }



    private val updateSessionDataDisplay: (List<SessionData>) -> Unit = { dataList: List<SessionData>->
        dataFileLayout.removeAllViewsInLayout() // clear out the old stuff to create an entirely new list

        var s = ""
        for (child in dataFileLayout.children)
            s += "$child; "
        Log.d(UI_TAG, "Data Table Children (before update): $s")
        Log.d(UI_TAG, "Session Data List Size: ${dataList.size}")

        val dataRows = mutableListOf<MutableList<SessionData>>()
        var buildRow = mutableListOf<SessionData>()
        for(data in dataList){
            buildRow.add(data)
            if(buildRow.size >= maxDataFilesPerRow){
                dataRows.add(buildRow)
                buildRow = mutableListOf() // type inferred
            }
        }
        if(buildRow.size > 0)
            dataRows.add(buildRow)

        for(dataRow in dataRows){
            layoutInflater.inflate(R.layout.data_file_row, dataFileLayout)
            val rowConstraintLayout = dataFileLayout[dataFileLayout.childCount-1] as ConstraintLayout // get the most recent child of the dataFileLayout
            val rowLinearLayout = rowConstraintLayout[0] as LinearLayout // the linear layout for the dataRow

            for(i in 0 until dataRow.size){ // to go each item (layout of a button/textView) in the dataRow that has an associated piece of data
                val itemLinearLayout = rowLinearLayout[i] as LinearLayout // get that item's linear layout
                val button = itemLinearLayout[0] as ImageButton // out of that, get the button and text view
                val textView = itemLinearLayout[1] as TextView
                val itemsData = dataRow[i] // get the associated data

                itemLinearLayout.visibility = View.VISIBLE // set this item visible, as it has associated data
                button.setOnClickListener {
                    itemsData.setSelected(!itemsData.isSelected)
                    if (itemsData.isSelected) {
                        button.setImageResource(R.drawable.file_selected)
                    }
                    else {
                        button.setImageResource(R.drawable.file_unselected)
                    }
                }
                textView.text = itemsData.sessionSubDir // set the label to the session sub directory (the session ID + duplicate number)

                Log.d(UI_TAG, "Row item $i: ${rowLinearLayout[i]}")
            }

            //rowLayout[0].visibility = View.GONE
            Log.d(UI_TAG, "Added dataRow (of size ${dataRow.size}) to data table")
        }

        s = ""
        for (child in dataFileLayout.children)
            s += "$child; "
        Log.d(UI_TAG, "Data Table Children (after update): $s")

        Log.d(UI_CLEAN_TAG, "Updated Data Display with: ${dataRows.size} rows, ${dataList.size} total items")
    }

    private val uploadSessionData: (SessionData)->Unit = { data: SessionData ->
        val uploadReadyData: MutableList<Unit> = data.formatForUpload()
        // TODO: Upload data to our Azure cloud

        Log.d(UI_CLEAN_TAG, "Data upload started for data directory: ${data.sessionSubDir}")
        Log.d(DATA_TAG, "Data upload started for data directory: ${data.sessionSubDir}")
        Toast.makeText(context, "Data upload started for: ${data.sessionSubDir}", Toast.LENGTH_SHORT).show()
    }


    private fun selectAllData(){
        Log.d(UI_CLEAN_TAG, "Selected all data files")
        return // TODO: Create select all functionality
    }

}