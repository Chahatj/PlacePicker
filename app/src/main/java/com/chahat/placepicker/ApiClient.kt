package com.chahat.placepicker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    companion object {
        var retrofit : Retrofit? = null

        fun getClient() : Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("http://maps.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }
}
