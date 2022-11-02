package com.psa.OakdResearchInterface

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.psa.OakdResearchInterface.data.SessionData
import com.psa.OakdResearchInterface.data.SessionDataManager
import com.psa.OakdResearchInterface.data.TCPClient
import com.psa.OakdResearchInterface.data.TCP_TAG
import com.psa.OakdResearchInterface.databinding.ActivityMainBinding
import com.psa.OakdResearchInterface.ui.main.SectionsPagerAdapter
import com.psa.OakdResearchInterface.ui.main.view_models.MasterViewModel
import org.xmlpull.v1.XmlPullParser


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mainViewModel: MasterViewModel by viewModels() // instantiate/setup the master view model here from the pool of view models
    private var tcpClient: TCPClient? = null
    private lateinit var sessionDataManager: SessionDataManager
   // private lateinit var dataButtonAttributeSet: AttributeSet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        // Setup TCP Connection
        ConnectTask{ tcpClient = it }.execute("") // pass in an expression that sets the tcp client
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


