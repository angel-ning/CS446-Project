package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.R
import com.example.spaceshare.databinding.ListingItemBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.utils.GeocoderUtil

class ListingAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<Listing, ListingAdapter.ViewHolder>(DiffCallback()) {

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

        fun bind(listing: Listing) {
            // Bind the listing data to the views
            binding.title.text = listing.title ?: "Untitled"
            binding.price.text = String.format("$%.2f CAD/day", listing.price)
            binding.spaceAvailable.text = "${listing.spaceAvailable} cubic metres"

            // Load the listing image from Firebase Storage into the ImageView
            if (listing.photos != null) {
                binding.viewPagerListingImages.adapter = ImageAdapter(listing.photos)
                binding.imageIndicator.setViewPager(binding.viewPagerListingImages)
            }

            // Set click listeners
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
