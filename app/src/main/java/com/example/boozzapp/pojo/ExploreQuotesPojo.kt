package com.example.boozzapp.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExploreQuotesPojo(

	@field:SerializedName("data")
	val data: ExploreQuotesData? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("status")
	val status: Boolean? = false
) : Parcelable

@Parcelize
data class ExploreQuotesTemplatesItem(

	@field:SerializedName("image")
	val image: String? = "",

	@field:SerializedName("image_url")
	val imageUrl: String? = "",

	@field:SerializedName("name")
	val name: String? = "",

	@field:SerializedName("id")
	val id: Int? = 0
) : Parcelable

@Parcelize
data class ExploreQuotesData(

	@field:SerializedName("templates")
	val templates: List<ExploreQuotesTemplatesItem?>? = emptyList(),

	@field:SerializedName("page_size")
	val pageSize: String? = ""
) : Parcelable
