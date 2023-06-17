package com.example.boozzapp.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeTemplate(

    @field:SerializedName("data")
    val data: TemplateData? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = false
) : Parcelable

@Parcelize
data class TemplateData(

    @field:SerializedName("templates")
    val templates: List<TemplatesItem?>? = emptyList(),

    @field:SerializedName("categories")
    val categories: List<CategoriesItem?>? = emptyList(),

    @field:SerializedName("page_size")
    val pageSize: String? = ""
) : Parcelable

@Parcelize
data class TemplatesItem(

    @field:SerializedName("zip")
    val zip: String? = "",

    @field:SerializedName("thumbnail")
    val thumbnail: String? = "",

    @field:SerializedName("is_new")
    val isNew: Boolean? = false,

    @field:SerializedName("video")
    val video: String? = "",

    @field:SerializedName("title")
    val title: String? = "",

    @field:SerializedName("thumbnail_url")
    val thumbnailUrl: String? = "",

    @field:SerializedName("zip_url")
    val zipUrl: String? = "",

    @field:SerializedName("video_webm")
    val videoWebm: String? = "",

    @field:SerializedName("video_url_webm")
    val videoUrlWebm: String? = "",

    @field:SerializedName("is_pinned")
    val isPinned: Int? = 0,

    @field:SerializedName("watermark_position")
    val watermarkPosition: String? = "",

    @field:SerializedName("video_url")
    val videoUrl: String? = "",

    @field:SerializedName("is_premium")
    val isPremium: Int? = 0,

    @field:SerializedName("is_hot")
    val isHot: Boolean? = false,

    @field:SerializedName("has_text")
    val hasText: Int? = 0,

    @field:SerializedName("width")
    val width: String? = "",

    @field:SerializedName("id")
    val id: Int? = 0,

    @field:SerializedName("height")
    val height: String? = ""
) : Parcelable

@Parcelize
data class CategoriesItem(

    @field:SerializedName("image_url")
    val imageUrl: String? = "",

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("id")
    val id: Int? = 0,

    @field:SerializedName("sort_by")
    val sortBy: String? = ""
) : Parcelable
