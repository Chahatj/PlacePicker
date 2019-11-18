package com.chahat.placepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_place_selection.*

class PlaceSelectionDialog(private val placeData: PlaceData,
                           private val selectionListener: PlaceSelectionListener) : DialogFragment() {

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
    }

    interface PlaceSelectionListener {
        fun onPlaceSelected(placeData: PlaceData)
    }
}
