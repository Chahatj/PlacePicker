package com.chahat.placepicker

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, "AIzaSyDkUPRCzN0sAjdoZXDLvUNAW3RBFHAODOw")
        placesClient = Places.createClient(this)
        findCurrentPlace()
    }

    private fun findCurrentPlace() {
        val placeFields = listOf(Place.Field.NAME)
        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        if (checkLocationPermission()) {
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener {
                if (it.isSuccessful) {
                    val response = it.result
                    for (placeLikelihood : PlaceLikelihood in response!!.placeLikelihoods) {
                        val placeName = placeLikelihood.place.name
                        Log.i("Place: ", placeName)
                    }
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
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findCurrentPlace()
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
