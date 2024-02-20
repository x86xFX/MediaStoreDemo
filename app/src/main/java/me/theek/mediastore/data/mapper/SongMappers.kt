package me.theek.mediastore.data.mapper

import me.theek.mediastore.data.local.entity.SongEntity
import me.theek.mediastore.domain.model.Song

fun SongEntity.toSong() : Song {
    return Song(
        id = id,
        songName = songName,
        artistName = artistName,
        albumName = albumName,
        duration = duration,
        path = path
    )
}

fun Song.toSongEntity() : SongEntity {
    return SongEntity(
        songName = songName,
        artistName = artistName,
        albumName = albumName,
        duration = duration,
        path = path
    )
}