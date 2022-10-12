package com.psa.OakdResearchInterface.ui.main.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.psa.OakdResearchInterface.R
import com.psa.OakdResearchInterface.databinding.FragmentConfigurationBinding
import com.psa.OakdResearchInterface.databinding.FragmentReviewBinding
import com.psa.OakdResearchInterface.ui.main.view_models.MasterViewModel



class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MasterViewModel by activityViewModels() // grab the already instantiated view model and create a reference for it here

    // View references
    // Declare view references here


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root = binding.root

        // Init button/view bindings here

        return root
    }

}