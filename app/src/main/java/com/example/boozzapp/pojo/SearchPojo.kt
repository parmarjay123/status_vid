package com.example.boozzapp.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchPojo(

	@field:SerializedName("data")
    val data: List<ExploreTemplatesItem?>? = emptyList(),

	@field:SerializedName("total_page")
    val totalPage: Int? = 0,

	@field:SerializedName("message")
    val message: String? = "",

	@field:SerializedName("status")
    val status: Boolean? = false
) : Parcelable


