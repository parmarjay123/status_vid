package com.example.boozzapp.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeCategoryPojo(

    @field:SerializedName("data")
    val data: List<DataItem?>? = emptyList()
) : Parcelable

@Parcelize
data class DataItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = "",

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("id")
    val id: Int? = 0,

    @field:SerializedName("sort_by")
    val sortBy: String? = ""
) : Parcelable
