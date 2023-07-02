package com.example.boozzapp.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotesTemplate(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = false
) : Parcelable

@Parcelize
data class Data(

    @field:SerializedName("templates")
    val templates: List<QuotesTemplatesItem?>? = emptyList(),

    @field:SerializedName("categories")
    val categories: List<QuotesCategoriesItem?>? = emptyList(),

    @field:SerializedName("page_size")
    val pageSize: Int? = 0
) : Parcelable

@Parcelize
data class QuotesCategoriesItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = "",

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("id")
    val id: Int? = 0,

    @field:SerializedName("sort_by")
    val sortBy: String? = ""
) : Parcelable

@Parcelize
data class QuotesTemplatesItem(

    @field:SerializedName("image")
    val image: String? = "",

    @field:SerializedName("image_url")
    val imageUrl: String? = "",

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("id")
    val id: Int? = 0
) : Parcelable
