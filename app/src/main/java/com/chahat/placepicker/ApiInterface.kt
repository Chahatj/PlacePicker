package com.chahat.placepicker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/maps/api/geocode/json")
    fun getAddressFromLatLng(@Query("latlng") latLng: String,
                             @Query("sensor") sensor: Boolean = true) : Call<AddressResponse>
}
