package com.dededev.machinetranslation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dededev.machinetranslation.data.ApiConfig
import com.dededev.machinetranslation.data.TranslateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _translated = MutableLiveData<TranslateResponse>()
    private val translated : LiveData<TranslateResponse> = _translated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun translate(query: String):LiveData<TranslateResponse> {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getApi(query)
        client.enqueue(object : Callback<TranslateResponse> {
            override fun onResponse(
                call: Call<TranslateResponse>,
                response: Response<TranslateResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _translated.value = responseBody
                    }
                } else {
                    Log.e("onResponse", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TranslateResponse>, t: Throwable) {
                Log.e("onResponse", "onFailure: ${t.message}")
            }
        })

        return translated
    }
}