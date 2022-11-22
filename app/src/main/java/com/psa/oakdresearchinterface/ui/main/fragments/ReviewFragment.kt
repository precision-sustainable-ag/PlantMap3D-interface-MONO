package com.psa.oakdresearchinterface.ui.main.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.psa.oakdresearchinterface.R
import com.psa.oakdresearchinterface.data.*
import com.psa.oakdresearchinterface.data.collection.CameraController
import com.psa.oakdresearchinterface.data.collection.OakDController
import com.psa.oakdresearchinterface.data.storage.SessionMetadata
import com.psa.oakdresearchinterface.databinding.FragmentReviewBinding
import com.psa.oakdresearchinterface.ui.main.dialogs.ConfirmationDialog
import com.psa.oakdresearchinterface.ui.main.view_models.MasterViewModel


class ReviewFragment : Fragment() {
    private val maxDataFilesPerRow = 5
    private val rowsPerScroll = 1
    private val rowDirectionalOffset = 1
    private val fullRowsPerPage = 3

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
    private var _dataScrollView: ScrollView? = null
    private val dataScrollView get() = _dataScrollView!!
    private var _scrollUpButton: ImageButton? = null
    private val scrollUpButton get() = _scrollUpButton!!
    private var _scrollDownButton: ImageButton? = null
    private val scrollDownButton get() = _scrollDownButton!!
    private var _dataFileLayout: LinearLayout? = null
    private val dataFileLayout get() = _dataFileLayout!!

    private lateinit var sessionDataManager: SessionDataManager
    private var currentSessionsData: SessionData? = null

    private var dataFileUIItems = mutableListOf<View>()
    private var dataFileButtons = mutableListOf<ImageButton>()
    private var dataRowCount = 0
    private var dataScrollRow = 0


    private lateinit var cameraController: CameraController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init session data manager
        sessionDataManager = SessionDataManager(requireContext(), updateSessionDataDisplay)

        // Setup test data
        /*val testDataList = mutableListOf<SessionData>()
        for(i in 0 until (maxDataFilesPerRow*10)+2){
            testDataList.add(SessionData(requireContext(), "TEST_SESSION($i)", SessionMetadata(), isExistingData = true))
        }
        sessionDataManager.addData(testDataList, updateUI = false) // don't update the UI yet, as the UI objects haven't been properly init-ed
        */


        // Add to update lists in onCreate to avoid adding to the list every time the tab UI is created/destroyed
        mainViewModel.selectAllButtonStateUpdateList.add{
            if(mainViewModel.selectAllButtonState.value == BUTTON_PRESSED || mainViewModel.selectAllButtonState.value == BUTTON_NOT_PRESSED)
                toggleSelectAll()
        }
        mainViewModel.uploadButtonStateUpdateList.add{
            if(mainViewModel.uploadButtonState.value == BUTTON_PRESSED)
                sessionDataManager.uploadSelectedData()
        }
        mainViewModel.deleteButtonStateUpdateList.add{
            if(mainViewModel.deleteButtonState.value == BUTTON_PRESSED){
                val dialog = ConfirmationDialog(requireContext(), R.string.cancel_option, R.string.delete_option) {
                    sessionDataManager.deleteSelectedData()
                }
                dialog.setCancelable(true)
                dialog.show()
            }
        }

        // Setup Camera Controller
        cameraController = OakDController(handleNewImage)

        mainViewModel.sessionRunStateUpdateList.add{
            if (mainViewModel.sessionRunState.value == COLLECT_RUNNING && mainViewModel.lastSessionRunState.value == COLLECT_PAUSED){
                cameraController.unpauseCollection()
            }
            else if(mainViewModel.sessionRunState.value == COLLECT_RUNNING){ // Only create new session data if starting fresh
                cameraController.startCollection()
                currentSessionsData = SessionData(
                    requireContext(),
                    "${mainViewModel.farmCode.value}_plot${mainViewModel.plotNumber.value}",
                    SessionMetadata(
                        farmCode = mainViewModel.farmCode.value!!,
                        plotNumber = mainViewModel.plotNumber.value!!,
                        seasonTiming = mainViewModel.seasonStage.value!!,
                        cashCrop = mainViewModel.cashCrop.value!!,
                        coverCrop = mainViewModel.coverCrop.value!!,
                        weatherCond = mainViewModel.weatherCond.value!!
                    ),
                    isExistingData = false
                )
                sessionDataManager.addData(currentSessionsData!!)
            }
            else if(mainViewModel.sessionRunState.value == COLLECT_PAUSED){
                cameraController.pauseCollection()
            }
            else {
                cameraController.stopCollection()
                currentSessionsData = null
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root = binding.root

        // Init button/view bindings here
        _selectAllButton = binding.selectAllButton
        selectAllButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if(!allSelected){
                    selectAllButton.setImageResource(R.drawable.select_all_button_clicked)
                    Log.d(UI_TAG, "Select all button: Pressed")
                    mainViewModel.setSelectAllButtonState(BUTTON_PRESSED)
                }
                else{
                    selectAllButton.setImageResource(R.drawable.select_all_button)
                    Log.d(UI_TAG, "Select all button: Released")
                    mainViewModel.setSelectAllButtonState(BUTTON_NOT_PRESSED)
                }
                //allSelected = !allSelected
            }

            false
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


        _dataScrollView = binding.fileScrollView
        _scrollUpButton = binding.scrollUpButton
        _scrollDownButton = binding.scrollDownButton
        scrollUpButton.setOnClickListener {
            Log.d(UI_TAG, "Scroll up button: Pressed")
            if(dataRowCount >= fullRowsPerPage) { // if a need to scroll at all
                dataScrollRow = maxOf(dataScrollRow - rowsPerScroll, 0)
                // scroll to the first item of the target row
                dataScrollView.scrollToDescendant(
                    dataFileUIItems[maxOf(dataScrollRow - rowDirectionalOffset, 0) * maxDataFilesPerRow]
                ) // the row offset accounts for the fact that the scroller only scrolls until the item is on screen
                Log.d(UI_TAG, "Scrolling up by $rowsPerScroll rows (to row $dataScrollRow)")
            }
        }
        scrollDownButton.setOnClickListener {
            Log.d(UI_TAG, "Scroll down button: Pressed")
            if(dataRowCount >= fullRowsPerPage){ // if a need to scroll at all
                dataScrollRow = minOf(dataScrollRow+rowsPerScroll, dataRowCount-1)
                // scroll to the first item of the target row
                Log.d(UI_TAG, "Data UI items: ${dataFileUIItems.size}")
                Log.d(UI_TAG, "Data Scroll Row: $dataScrollRow")
                Log.d(UI_TAG, "Data Row Count: $dataRowCount")

                dataScrollView.scrollToDescendant(
                    dataFileUIItems[minOf(dataScrollRow+rowDirectionalOffset,dataRowCount-1) * maxDataFilesPerRow]
                ) // the row offset accounts for the fact that the scroller only scrolls until the item is on screen
                Log.d(UI_TAG, "Scrolling down by $rowsPerScroll rows (to row $dataScrollRow)")
            }
        }


        // Update Session Data Display
        _dataFileLayout = binding.dataFileLinearLayout
        sessionDataManager.updateDataUI()



        return root
    }



    private var allSelected = false
    private fun toggleSelectAll(){
        val targetSelectState = !allSelected
        // If want all selected, click all resource, if not, default resource
        val targetSelectAllResource = if(targetSelectState) R.drawable.file_selected else R.drawable.file_unselected

        // items in the sessionDataList have indexes that match with their corresponding buttons
        for(i in 0 until minOf(dataFileButtons.size, sessionDataManager.sessionDataList.size)){
            if(sessionDataManager.sessionDataList[i].isSelected != targetSelectState){
                sessionDataManager.sessionDataList[i].setSelected(targetSelectState)
                dataFileButtons[i].setImageResource(targetSelectAllResource)
            }
        }

        allSelected = targetSelectState
        Log.d(UI_CLEAN_TAG, "${if(!allSelected) "Des" else "S"}elected all data files.")
    }



    private val updateSessionDataDisplay: (List<SessionData>) -> Unit = { dataList: List<SessionData>->
        dataFileLayout.removeAllViewsInLayout() // clear out the old stuff to create an entirely new list

        //var s = ""
        //for (child in dataFileLayout.children)
        //    s += "$child; "
        //Log.d(UI_TAG, "Data Table Children (before update): $s")
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
        dataRowCount = dataRows.size // save the number of rows for scrolling purposes

        dataFileButtons = mutableListOf()
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
                    itemsData.setSelected(!itemsData.isSelected) // toggle selected state
                    if (itemsData.isSelected) {
                        button.setImageResource(R.drawable.file_selected)
                    }
                    else {
                        button.setImageResource(R.drawable.file_unselected)
                    }
                }
                textView.text = itemsData.sessionID // set the label to the session sub directory (the session ID + duplicate number)

                dataFileUIItems.add(itemLinearLayout)
                dataFileButtons.add(button)
                //Log.d(UI_TAG, "Row item $i: ${rowLinearLayout[i]}")
            }

            Log.d(UI_TAG, "Added dataRow (of size ${dataRow.size}) to data table")
        }

        dataScrollRow = 0
        if(dataRowCount >= fullRowsPerPage) // if enough rows to need a scroll bar, have it visible
            binding.scrollLinearLayout.visibility = View.VISIBLE
        else // else, do not have it exist
            binding.scrollLinearLayout.visibility = View.GONE

        //s = ""
        //for (child in dataFileLayout.children)
        //    s += "$child; "
        //Log.d(UI_TAG, "Data Table Children (after update): $s")

        Log.d(UI_CLEAN_TAG, "Updated Data Display with: ${dataRows.size} rows, ${dataList.size} total items")
    }

    private val handleNewImage: (Bitmap)->Unit = { newImg: Bitmap ->
        // TODO: Have a universal way to handle a new piece of image data input into the system
    }
}