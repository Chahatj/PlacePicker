package com.chahat.placepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.dialog_place_selection.*

class PlaceSelectionDialog(private val placeData: PlaceData,
                           private val selectionListener: PlaceSelectionListener)
    : DialogFragment(), OnMapReadyCallback {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_place_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_view_place_name.text = placeData.name
        text_view_place_address.text = placeData.address
        text_view_cancel.setOnClickListener {
            dismiss()
        }
        text_view_ok.setOnClickListener {
            selectionListener.onPlaceSelected(placeData)
        }

        val mapFragment : SupportMapFragment =
            fragmentManager!!.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        val markerOptions = MarkerOptions()
        markerOptions.position(placeData.latLng)
        p0?.animateCamera(CameraUpdateFactory.newLatLng(placeData.latLng))
        p0?.addMarker(markerOptions)
    }

    interface PlaceSelectionListener {
        fun onPlaceSelected(placeData: PlaceData)
    }
}
