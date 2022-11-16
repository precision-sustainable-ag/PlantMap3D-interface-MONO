package com.psa.oakdresearchinterface.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psa.oakdresearchinterface.R
import com.psa.oakdresearchinterface.ui.main.fragments.CollectionFragment
import com.psa.oakdresearchinterface.ui.main.fragments.ConfigurationFragment
import com.psa.oakdresearchinterface.ui.main.fragments.PlaceholderFragment
import com.psa.oakdresearchinterface.ui.main.fragments.ReviewFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        return when (position) {
            0 ->  ConfigurationFragment()
            1 -> CollectionFragment()
            2 -> ReviewFragment()
            else -> PlaceholderFragment.newInstance(position + 1) // If nothing else return a PlaceholderFragment.
        }
        // If nothing else return a PlaceholderFragment (defined as a static inner class below).
    }

    fun getTabTitle(position: Int): String {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getItemCount(): Int {
        // Show 3 total pages.
        return 3
    }
}