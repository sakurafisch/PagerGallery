package com.winnerwinter.gallery

import com.google.gson.annotations.SerializedName

data class Pixabay(
    val total: Int,
    val totalHits: Int,
    @SerializedName("hits") val photoItems: Array<PhotoItem>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pixabay

        if (!photoItems.contentEquals(other.photoItems)) return false
        if (total != other.total) return false
        if (totalHits != other.totalHits) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoItems.contentHashCode()
        result = 31 * result + total
        result = 31 * result + totalHits
        return result
    }
}

data class PhotoItem(
    @SerializedName("webformatURL") val previewURL: String,
    @SerializedName("id") val photoId: Int,
    @SerializedName("largeImageURL") val fullUrl: String
)