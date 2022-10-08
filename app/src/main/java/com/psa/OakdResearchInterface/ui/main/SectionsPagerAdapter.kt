package com.psa.OakdResearchInterface.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.psa.OakdResearchInterface.R
import com.psa.OakdResearchInterface.ui.main.fragments.ConfigurationFragment
import com.psa.OakdResearchInterface.ui.main.fragments.PlaceholderFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        return when (position) {
            0 ->  ConfigurationFragment()
            // 1 -> CollectionFragment()
            // 2 -> ReviewFragment()
            else -> PlaceholderFragment.newInstance(position + 1) // If nothing else return a PlaceholderFragment.
        }
        // If nothing else eturn a PlaceholderFragment (defined as a static inner class below).

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 3
    }
}