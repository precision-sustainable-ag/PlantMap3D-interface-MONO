package com.psa.oakdresearchinterface.ui.main.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.psa.oakdresearchinterface.R
import com.psa.oakdresearchinterface.data.*
import com.psa.oakdresearchinterface.databinding.FragmentCollectionBinding
import com.psa.oakdresearchinterface.ui.main.view_models.MasterViewModel


class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MasterViewModel by activityViewModels() // grab the already instantiated view model and create a reference for it here

    // View references
    private var _startButton: ImageButton? = null
    private val startButton get() = _startButton!!
    private var _stopButton: ImageButton? = null
    private val stopButton get() = _stopButton!!
    private var _unpauseButton: ImageButton? = null
    private val unpauseButton get() = _unpauseButton!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.sessionRunStateUpdateList.add { // update button images/visibilities
            when (mainViewModel.sessionRunState.value) {
                COLLECT_RUNNING -> { // if now running
                    startButton.setImageResource(R.drawable.pause_button)
                    unpauseButton.visibility = View.GONE
                    stopButton.setImageResource(R.drawable.stop_button)
                }
                COLLECT_PAUSED -> { // if now paused
                    startButton.setImageResource(R.drawable.pause_button_clicked)
                    unpauseButton.visibility = View.VISIBLE
                    stopButton.setImageResource(R.drawable.stop_button)
                }
                COLLECT_NOT_STARTED -> { // if now stopped
                    startButton.setImageResource(R.drawable.start_button)
                    unpauseButton.visibility = View.GONE
                    stopButton.setImageResource(R.drawable.stop_button_clicked)
                }
            }
        }

        mainViewModel.sessionRunStateUpdateList.add { // print the session state
            Log.d(UI_CLEAN_TAG, "Session is now: ${mainViewModel.sessionRunState.value}")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        val root = binding.root

        _startButton = binding.startButton
        startButton.setOnClickListener {
            Log.d(UI_TAG, "Start/pause button: Pressed")
            if(mainViewModel.sessionRunState.value == COLLECT_NOT_STARTED || mainViewModel.sessionRunState.value == null) // if not running, the start button should start the session
                mainViewModel.setSessionRunState(COLLECT_RUNNING)
            else if(mainViewModel.sessionRunState.value == COLLECT_RUNNING) // if running, the start button should act as the pause button
                mainViewModel.setSessionRunState(COLLECT_PAUSED)
        }

        _stopButton = binding.stopButton
        stopButton.setOnClickListener {
            Log.d(UI_TAG, "Stop button: Pressed")
            if(mainViewModel.sessionRunState.value != COLLECT_NOT_STARTED) // don't stop if already stopped
                mainViewModel.setSessionRunState(COLLECT_NOT_STARTED)
        }

        _unpauseButton = binding.unpauseButton
        unpauseButton.setOnClickListener {
            Log.d(UI_TAG, "Unpause button: Pressed")
            if(mainViewModel.sessionRunState.value == COLLECT_PAUSED) // if paused, unpause
                mainViewModel.setSessionRunState(COLLECT_RUNNING)
        }

        return root
    }




}
