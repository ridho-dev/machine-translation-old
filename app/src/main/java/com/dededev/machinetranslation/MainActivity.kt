package com.dededev.machinetranslation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.dededev.machinetranslation.data.ApiConfig
import com.dededev.machinetranslation.data.TranslateResponse
import com.dededev.machinetranslation.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var translatedText: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        translatedText = 0

        val inputText = binding.inputSource
        val outputText = binding.outputTarget
        val translation = binding.outputTargetLayout
        val btnSubmit = binding.btnSubmit
        val btnChange = binding.changeBtn

        outputText.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                "Not yet implemented"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                "Not yet implemented"
                if (p0 != null) {
                    if (p0.isNotEmpty()) {
                        btnSubmit.visibility = View.VISIBLE
                    } else {
                        translation.visibility = View.GONE
                        btnSubmit.visibility = View.GONE
                        outputText.setText("")
                        btnChange.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }

        inputText.addTextChangedListener(textWatcher)

        btnSubmit.setOnClickListener {
            translation.visibility = View.VISIBLE
            btnChange.visibility = View.VISIBLE
            translate(inputText.text.toString())
        }

        btnChange.setOnClickListener {
            val temp: String = inputText.text.toString()
            inputText.text = outputText.text
            outputText.setText(temp)

            val sourceLanguage = binding.sourceLanguage
            if (sourceLanguage.text == getString(R.string.indonesia)) {
                binding.apply {
                    sourceLanguage.text = getString(R.string.batak_toba)
                    targetLanguage.text = getString(R.string.indonesia)
                }
            } else {
                binding.apply {
                    sourceLanguage.text = getString(R.string.indonesia)
                    targetLanguage.text = getString(R.string.batak_toba)
                }
            }
        }

    }

    private fun translate(inputText: String) {
        val outputText = binding.outputTarget

        val client = ApiConfig.getApiService().getApi(0)
        client.enqueue(object : Callback<TranslateResponse> {
            override fun onResponse(
                call: Call<TranslateResponse>,
                response: Response<TranslateResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        outputText.setText(responseBody.activity)
                    }
                } else {
                    Log.e("onResponse", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TranslateResponse>, t: Throwable) {
                Log.e("onResponse", "onFailure: ${t.message}")
            }
        })
    }
}
