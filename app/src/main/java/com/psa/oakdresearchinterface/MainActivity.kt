package com.psa.oakdresearchinterface

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

        val sectionsPagerAdapter = SectionsPagerAdapter(this, this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = sectionsPagerAdapter.getTabTitle(position)
        }.attach()

        _collectionStatusTextView = binding.collectionStatusView
        mainViewModel.sessionRunStateUpdateList.add{
            collectionStatusTextView.text = mainViewModel.sessionRunState.value
        }

        // Setup TCP Connection
       // ConnectTask{ tcpClient = it }.execute("") // pass in an expression that sets the tcp client TODO( Move to OakDController )


    }



}


