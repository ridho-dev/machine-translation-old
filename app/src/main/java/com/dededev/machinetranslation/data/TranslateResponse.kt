package com.dededev.machinetranslation.data

import com.google.gson.annotations.SerializedName

data class TranslateResponse(

	@field:SerializedName("input")
	val input: String,

	@field:SerializedName("output")
	val output: String,
)
