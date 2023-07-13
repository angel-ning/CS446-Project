package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.databinding.ListingItemBinding
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.ui.view.ListingMetadataDialogFragment
import java.util.Objects

class ReservationAdapter(
    private val childFragmentManager: FragmentManager,
    private val itemClickListener: ItemClickListener
) : ListAdapter<Listing, ReservationAdapter.ViewHolder>(DiffCallback()) {

    var areEditButtonsGone = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListingItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = getItem(position)
        holder.bind(listing)
    }

    inner class ViewHolder(private val binding: ListingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: Listing, reservation: Reservation) {
            // Bind the listing data to the views
            binding.title.text = listing.title ?: "Untitled"
            binding.price.text = String.format("$%.2f CAD/day", listing.price)

            // Load the listing image from Firebase Storage into the ImageView
            binding.viewPagerListingImages.adapter =
                ImageAdapter(listing.photos.map { ImageModel(imagePath = it) })
            binding.imageIndicator.setViewPager(binding.viewPagerListingImages)

            // Whether to show the edit button
            binding.btnEdit.isGone = areEditButtonsGone

            binding.

            // Set click listeners
            binding.btnEdit.setOnClickListener {
                println("CLick")
                val listingMetadataDialogFragment = ListingMetadataDialogFragment(listing)
                listingMetadataDialogFragment.show(
                    Objects.requireNonNull(childFragmentManager),
                    "listingMetadataDialog"
                )
            }
            binding.textContainer.setOnClickListener {
                itemClickListener.onItemClick(listing)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(listing: Listing)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Listing>() {
        override fun areItemsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Listing, newItem: Listing): Boolean {
            return oldItem == newItem
        }
    }
}