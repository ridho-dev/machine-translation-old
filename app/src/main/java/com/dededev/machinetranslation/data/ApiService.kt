package com.dededev.machinetranslation.data

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("api/activity")
    fun getApi(
        @Query("price") price: Int
    ): Call<TranslateResponse>
}