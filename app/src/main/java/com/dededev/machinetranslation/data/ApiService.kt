package com.dededev.machinetranslation.data

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("indoToBatak")
    fun indoToBatak(
        @Query("s") s: String
    ): Call<TranslateResponse>

    @GET("batakToIndo")
    fun batakToIndo(
        @Query("s") s: String
    ): Call<TranslateResponse>
}