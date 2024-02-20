package me.theek.mediastore.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "song_name") val songName: String?,
    @ColumnInfo(name = "artist_name") val artistName: String?,
    @ColumnInfo(name = "album_name") val albumName: String?,
    val duration: Int?,
    val path: String,
    @ColumnInfo(name = "added_date") val addedDate: Long = System.currentTimeMillis()
)
