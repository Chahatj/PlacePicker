package com.chahat.placepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val AUTOCOMPLETE_REQUEST_CODE = 2

    private lateinit var placesClient: PlacesClient
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: View
    private lateinit var nearbyPlaceAdapter: NearbyPlaceAdapter

    private var currentMarker : Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.view!!

        Places.initialize(applicationContext, "AIzaSyDkUPRCzN0sAjdoZXDLvUNAW3RBFHAODOw")
        placesClient = Places.createClient(this)

        initUI()
        findCurrentPlace()
    }

    private fun initUI() {
        text_view_search.setOnClickListener {
            openAutoCompleteActivity()
        }
        text_view_select_location.setOnClickListener {

        }
        nearbyPlaceAdapter = NearbyPlaceAdapter(
            mutableListOf(),
            object : NearbyPlaceAdapter.NearbyPlaceClickListener {
                override fun onClick(place: Place) {
                    addMarkerOnMap(place.latLng!!)
                }
            }
        )
        recycler_view_nearby_place.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false)
        recycler_view_nearby_place.addItemDecoration(ItemOffsetDecoration(40))
        recycler_view_nearby_place.adapter = nearbyPlaceAdapter
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap!!
        this.googleMap.isMyLocationEnabled = true
        this.googleMap.uiSettings.isMyLocationButtonEnabled = true
        val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View)
            .findViewById<View>(Integer.parseInt("2"))
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30)

        this.googleMap.setOnMapClickListener {
            addMarkerOnMap(it)
        }

        if (!checkLocationPermission()) {
            requestLocationPermission()
        }
        getDeviceLocation()
    }

    private fun addMarkerOnMap(latLng : LatLng) {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        this.googleMap.clear()
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        this.currentMarker = this.googleMap.addMarker(markerOptions)
    }

    private fun findCurrentPlace() {
        val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        if (checkLocationPermission()) {
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener {
                val response = it.result
                if (it.isSuccessful && response != null) {
                    nearbyPlaceAdapter.setNearbyPlaces(response.placeLikelihoods)
                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    fun getDeviceLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val lastKnownLocation = it.result
                val latLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation.longitude)
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun checkLocationPermission() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun openAutoCompleteActivity() {
        val fields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val place = Autocomplete.getPlaceFromIntent(data)
                text_view_search.text = place.address
                addMarkerOnMap(place.latLng!!)
            } else {
                Toast.makeText(
                    this,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(
                    this,
                    "Please provide location permission",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
