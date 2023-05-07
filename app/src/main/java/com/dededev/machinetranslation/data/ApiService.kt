package com.dededev.machinetranslation.data

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("translate")
    fun getApi(
        @Query("s") s: String
    ): Call<TranslateResponse>
}