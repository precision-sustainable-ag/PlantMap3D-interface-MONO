package com.psa.oakdresearchinterface

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.psa.oakdresearchinterface.data.*
import com.psa.oakdresearchinterface.databinding.ActivityMainBinding
import com.psa.oakdresearchinterface.ui.main.SectionsPagerAdapter
import com.psa.oakdresearchinterface.ui.main.view_models.MasterViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MasterViewModel by viewModels() // instantiate/setup the master view model here from the pool of view models

    private var _collectionStatusTextView: TextView? = null
    private val collectionStatusTextView get() = _collectionStatusTextView!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        _collectionStatusTextView = binding.collectionStatusView
        mainViewModel.sessionRunStateUpdateList.add{
            collectionStatusTextView.text = mainViewModel.sessionRunState.value
        }

        // Setup TCP Connection
       // ConnectTask{ tcpClient = it }.execute("") // pass in an expression that sets the tcp client TODO( Move to OakDController )


    }



    class ConnectTask (private val setTcpClientFunc: (TCPClient)->Unit) : AsyncTask<String?, String?, TCPClient?>(){
        override fun doInBackground(vararg params: String?): TCPClient? {

            //we create a TCPClient object and
            var tcpClient = TCPClient(object : TCPClient.OnMessageReceived {
                //here the messageReceived method is implemented
                override fun messageReceived(message: String?) {
                    //this method calls the onProgressUpdate
                    publishProgress(message)
                }
            })
            setTcpClientFunc(tcpClient) // sets the tcp client inside of the main scope, via the passed in setter function
            tcpClient.run()
            return null
        }

        override fun onProgressUpdate(vararg values: String?) { // handles the messages being received
            super.onProgressUpdate(*values)

            Log.d(TCP_TAG, "Client - Message Handler Received: ${values[1]}")

            // insert code to handle updates here
        }
    }


}


