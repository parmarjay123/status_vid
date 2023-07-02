package com.example.boozzapp.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotesCategory(
	val data: List<QuoteCategoryList?>? = emptyList()
) : Parcelable

@Parcelize
data class QuoteCategoryList(
	val imageUrl: String? = "",
	val name: String? = "",
	val id: Int? = 0,
	val sortBy: String? = ""
) : Parcelable
