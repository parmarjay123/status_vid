package com.example.boozzapp.pojo

import org.json.JSONArray

data class ImageCommands(
    var imgName: String,
    var imgHeight: Int,
    var imgWidth: Int,
    var imgPath: String,
    var prefix: JSONArray,
    var postfix: JSONArray
) {
    var imgPathExtra: String? = null
    var changesOccurs: Boolean = false

    constructor(imgName: String, prefix: JSONArray) : this(imgName, 0, 0, "", prefix, JSONArray()) {
        // Secondary constructor calling the primary constructor with default values
    }
}
