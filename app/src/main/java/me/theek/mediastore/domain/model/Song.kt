package me.theek.mediastore.domain.model

data class Song(
    val id: Int,
    val songName: String?,
    val artistName: String?,
    val albumName: String?,
    val duration: Int?,
    val path: String
)
