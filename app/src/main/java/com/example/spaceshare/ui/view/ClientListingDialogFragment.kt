package com.example.spaceshare.ui.view

import MapDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ImageAdapter
import com.example.spaceshare.data.repository.MessagesRepository
import com.example.spaceshare.databinding.DialogClientListingBinding
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.example.spaceshare.utils.GeocoderUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class ClientListingDialogFragment(
    private val listing: Listing,
    private val searchViewModel: SearchViewModel? = null,
    private val hideMessageHostButton: Boolean = false
): DialogFragment(), OnMapReadyCallback {

    companion object {
        private val TAG = this::class.simpleName
    }

    @Inject
    lateinit var messagesViewModel: MessagesViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    private lateinit var binding: DialogClientListingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_client_listing, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        configureBindings()
        configureButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
    }

    private fun configureBindings() {
        binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })
        binding.imageIndicator.setViewPager(binding.viewPagerListingImages)
        binding.titleText.text = listing.title
        binding.location.text = listing.location?.let { location ->
            GeocoderUtil.getGeneralLocation(location.latitude, location.longitude)
        }
        binding.price.text = getString(R.string.listing_price_template, listing.price)
        binding.likes.text = listing.likes.toString()

        // If no amenities, we do not require a divider for this section
        if (listing.amenities.isEmpty()) {
            binding.divider1.visibility = View.GONE
        }

        // Filter amenities that are not present
        val absentAmenities = Amenity.values().filterNot { listing.amenities.contains(it) }
        for (amenity in absentAmenities) {
            when (amenity) {
                Amenity.SURVEILLANCE -> binding.surveillance.visibility = View.GONE
                Amenity.CLIMATE_CONTROLLED -> binding.climateControlled.visibility = View.GONE
                Amenity.WELL_LIT-> binding.lighting.visibility = View.GONE
                Amenity.ACCESSIBILITY -> binding.accessibility.visibility = View.GONE
                Amenity.WEEKLY_CLEANING -> binding.cleanliness.visibility = View.GONE
            }
        }

        binding.description.text = listing.description
        binding.mapView.getMapAsync { map ->
            listing.location?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }

        runBlocking {
            if (listing.hostId != null) {
                val verified = profileViewModel.getUserVerifiedStatus(listing.hostId)
                if (verified.toInt() == 1) {
                    binding.verifiedStatus.text = getString(R.string.dialog_id_verified_true)
                    binding.verifiedStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
                } else {
                    binding.verifiedStatus.text = getString(R.string.dialog_id_verified_false)
                    binding.verifiedStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exclamation_circle, 0, 0, 0)
                }
            }
        }
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }

        binding.btnReserve.setOnClickListener {
            val reservationPageDialogFragment = ReservationPageDialogFragment(listing, searchViewModel)
//            val bundle = Bundle().apply {
//                putInt("reservationId", reservationId)
//            }
//            dialogFragment.arguments = bundle
//            reservationPageDialogFragment.show(supportFragmentManager, "ReservationDetailDialogFragment")
            reservationPageDialogFragment.show(Objects.requireNonNull(childFragmentManager),
                "ReservationDetailDialogFragment")
        }


        binding.btnMessageHost.isGone = hideMessageHostButton
        binding.btnMessageHost.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val chat = messagesViewModel.createChatWithHost(listing)
                val chatDialogFragment = ChatDialogFragment(chat, shouldRefreshChatsList = false)
                chatDialogFragment.show(
                    childFragmentManager,
                    "chatDialog"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.isScrollGesturesEnabled = false

        // Add a marker at a specific location
        listing.location?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            map?.addMarker(MarkerOptions().position(latLng))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            map.setOnMapClickListener {
                val mapDialogFragment = MapDialogFragment(null, latLng, true)
                mapDialogFragment.show(Objects.requireNonNull(childFragmentManager), "mapDialog")
            }
        }
    }
}