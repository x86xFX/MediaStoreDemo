package me.theek.mediastore.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.theek.mediastore.data.local.SongDao
import me.theek.mediastore.data.mapper.toSong
import me.theek.mediastore.data.mapper.toSongEntity
import me.theek.mediastore.data.service.MediaStoreReader
import me.theek.mediastore.domain.model.Song
import me.theek.mediastore.domain.repository.FlowEvent
import me.theek.mediastore.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSongRepository @Inject constructor(
    private val mediaStoreReader: MediaStoreReader,
    private val songDao: SongDao
) : SongRepository {

    override fun getSongs(): Flow<FlowEvent<List<Song>>> = flow<FlowEvent<List<Song>>> {

        Log.d("LocalSongRepository", "Flow executed on ${Thread.currentThread().name}")

        val existingSongs = songDao.getAllSongs().first()

        if (existingSongs.isEmpty()) {
            mediaStoreReader.getSongs().collect { state ->
                when (state) {
                    is FlowEvent.Progress -> {
                        emit(
                            FlowEvent.Progress(
                                size = state.size,
                                progress = state.progress,
                                message = state.message
                            )
                        )
                    }
                    is FlowEvent.Success -> {
                        state.data.onEach { songs ->
                            songDao.insertSongs(songs.toSongEntity())
                        }

                        emit(FlowEvent.Success(state.data))
                    }
                }
            }

        } else {
            emit(
                FlowEvent.Success(
                    existingSongs.map { it.toSong() }
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getSongCover(songPath: String): ByteArray? {
        return mediaStoreReader.getSongCover(songPath)
    }
}