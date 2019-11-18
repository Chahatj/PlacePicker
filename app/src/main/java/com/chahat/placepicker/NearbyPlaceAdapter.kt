package com.chahat.placepicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.PlaceLikelihood
import kotlinx.android.synthetic.main.place.view.*

class NearbyPlaceAdapter(private val nearbyPlaces: MutableList<PlaceLikelihood>)
    : RecyclerView.Adapter<NearbyPlaceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.place, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = nearbyPlaces.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = nearbyPlaces[position].place
        holder.textViewPlaceName.text = place.name
        holder.textViewPlaceAddress.text = place.address
    }

    fun setNearbyPlaces(placeLikelihoods : List<PlaceLikelihood>) {
        this.nearbyPlaces.clear()
        this.nearbyPlaces.addAll(placeLikelihoods)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPlaceName : TextView = view.text_view_place_name
        val textViewPlaceAddress : TextView = view.text_view_place_address
    }
}
