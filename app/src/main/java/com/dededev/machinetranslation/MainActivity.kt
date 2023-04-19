package com.dededev.machinetranslation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dededev.machinetranslation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inputText = binding.inputSource
        val outputText = binding.outputTarget
        val translation = binding.outputTargetLayout

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
                        translation.visibility = View.VISIBLE
                        outputText.text = inputText.text.toString()
                    } else {
                        translation.visibility = View.GONE
                    }
                }
            }
        }

        inputText.addTextChangedListener(textWatcher)

    }
}