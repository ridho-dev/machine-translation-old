package com.dededev.machinetranslation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dededev.machinetranslation.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech

    private lateinit var btnSubmit : ImageButton
    private lateinit var btnChange : ImageButton
    private lateinit var btnSourceVoice : ImageButton
    private lateinit var btnTargetVoice : ImageButton
    private lateinit var micFab : ImageButton

    private lateinit var translation : ConstraintLayout
    private lateinit var inputText : EditText
    private lateinit var outputText : EditText
    private lateinit var sourceLanguage : TextView
    private lateinit var targetLanguage : TextView


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbMain
        setSupportActionBar(toolbar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.bg_red)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        inputText = binding.inputSource
        outputText = binding.outputTarget
        translation = binding.outputTargetLayout
        btnSubmit = binding.btnSubmit
        btnChange = binding.changeBtn
        sourceLanguage = binding.sourceLanguage
        targetLanguage = binding.targetLanguage
        btnSourceVoice = binding.btnSourceVoice
        btnTargetVoice = binding.btnTargetVoice
        micFab = binding.micFab

        outputText.isEnabled = false
        btnTargetVoice.isEnabled = targetLanguage.text != getString(R.string.batak_toba)

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

        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.forLanguageTag("id")
            }
        }

        btnSubmit.setOnClickListener(clickListener)
        btnChange.setOnClickListener(clickListener)
        btnSourceVoice.setOnClickListener(clickListener)
        btnTargetVoice.setOnClickListener(clickListener)
        micFab.setOnClickListener(clickListener)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mainProgressBar.visibility = View.VISIBLE
            translation.visibility = View.GONE
            btnChange.visibility = View.GONE
        } else {
            binding.mainProgressBar.visibility = View.GONE
            translation.visibility = View.VISIBLE
            btnChange.visibility = View.VISIBLE
        }
    }

    private val clickListener = View.OnClickListener { view ->  
        when (view.id) {
            R.id.btn_submit -> {
                mainViewModel.translate(inputText.text.toString()).observe(this) {
                    outputText.setText(it.output)
                }

            }
            R.id.change_btn -> {
                val temp: String = inputText.text.toString()
                inputText.text = outputText.text
                outputText.setText(temp)

                if (sourceLanguage.text == getString(R.string.indonesia)) {
                    binding.apply {
                        sourceLanguage.text = getString(R.string.batak_toba)
                        targetLanguage.text = getString(R.string.indonesia)

                        btnSourceVoice.isEnabled = false
                        btnTargetVoice.isEnabled = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            btnSourceVoice.imageTintList = ColorStateList.valueOf(getColor(R.color.disabled_button_color))
                            btnTargetVoice.imageTintList = ColorStateList.valueOf(getColor(R.color.main_red))
                            btnSourceVoice.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.transparent))
                            micFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.disabled_button_color))
                        }
                    }
                } else {
                    binding.apply {
                        sourceLanguage.text = getString(R.string.indonesia)
                        targetLanguage.text = getString(R.string.batak_toba)

                        btnSourceVoice.isEnabled = true
                        btnTargetVoice.isEnabled = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            btnSourceVoice.imageTintList = ColorStateList.valueOf(getColor(R.color.main_red))
                            btnTargetVoice.imageTintList = ColorStateList.valueOf(getColor(R.color.disabled_button_color))
                            btnSourceVoice.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.bg_red))
                            micFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.main_red))

                        }
                    }
                }
            }
            R.id.mic_fab -> {
                if (binding.sourceLanguage.text == getString(R.string.batak_toba)) {
                    Toast.makeText(
                        applicationContext,
                        "Maaf, Speech to Text untuk Bahasa Batak Toba belum tersedia",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
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
            }
            R.id.btn_source_voice -> {
                val text = inputText.text
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
            R.id.btn_target_voice -> {
                val text = outputText.text
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
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
                        mainViewModel.translate(spokenText).observe(this) {
                            outputText.setText(it.output)
                        }
                    }
                }
            }
        }
    }
}
