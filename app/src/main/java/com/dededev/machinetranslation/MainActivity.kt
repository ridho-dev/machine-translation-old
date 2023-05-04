package com.dededev.machinetranslation

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dededev.machinetranslation.data.ApiConfig
import com.dededev.machinetranslation.data.TranslateResponse
import com.dededev.machinetranslation.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech
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

            }

            override fun afterTextChanged(p0: Editable?) {
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

        binding.micFab.setOnClickListener {
            val microphone = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            microphone.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            microphone.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id")

            try {
                inputText.setText("")
                startActivityForResult(microphone, 100)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    applicationContext,
                    "Perangkat Anda tidak mendukung fitur ini",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.forLanguageTag("id")
            }
        }

        binding.btnSourceVoice.setOnClickListener {
            val text = inputText.text
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            100 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val spokenText = result?.get(0)
                    // Set hasil transkripsi suara ke dalam TextView

                    if (spokenText != null) {
                        binding.apply {
                            inputSource.setText(spokenText)
                            changeBtn.visibility = View.VISIBLE
                            outputTargetLayout.visibility = View.VISIBLE
                        }
                        translate(spokenText)
                    }
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
