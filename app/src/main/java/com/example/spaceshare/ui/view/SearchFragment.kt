package com.example.spaceshare.ui.view

import MapDialogFragment
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentSearchBinding
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Objects

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var navController: NavController
    private val searchViewModel : SearchViewModel by viewModels()

    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)
        geocoder = Geocoder(requireContext())

        configureCards()
    }

    private fun configureCards() {
        // Where
        binding.whereCard.setOnClickListener {
            hideWhatSelectorCard()
            val mapDialogFragment = MapDialogFragment(searchViewModel)
            mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
        }
        searchViewModel.location.observe(viewLifecycleOwner) { location ->
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                binding.searchLocation.text = address.getAddressLine(0)
            }
        }

        // When
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Dates")
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
        dateRangePicker.addOnPositiveButtonClickListener {
            binding.searchTime.text = dateRangePicker.headerText
        }
        binding.whenCard.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, "tag")
            hideWhatSelectorCard()
        }

        // What
        binding.whatCard.setOnClickListener {
            it.isGone = true
            binding.whatSelectorCard.isGone = false
        }
        binding.whatSelectorAddSize.setOnClickListener {
            searchViewModel.incrementSpaceRequired()
        }
        binding.whatSelectorMinusSize.setOnClickListener {
            searchViewModel.decrementSpaceRequired()
        }
        searchViewModel.spaceRequired.observe(viewLifecycleOwner) { spaceRequired ->
            binding.whatSelectorSizeText.text = spaceRequired.toString()
        }
    }

    private fun hideWhatSelectorCard() {
        binding.whatCard.isGone = false
        binding.whatSelectorCard.isGone = true
    }

}