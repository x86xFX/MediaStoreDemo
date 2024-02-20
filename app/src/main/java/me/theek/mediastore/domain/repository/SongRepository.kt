package me.theek.mediastore.domain.repository

import kotlinx.coroutines.flow.Flow
import me.theek.mediastore.domain.model.Song
import kotlin.math.max
import kotlin.math.min

interface SongRepository {
    fun getSongs() : Flow<FlowEvent<List<Song>>>
    suspend fun getSongCover(songPath: String) : ByteArray?
}

sealed interface FlowEvent<T> {
    data class Progress<T>(
        val size: Int,
        val progress: Int,
        val message: String?
    ) : FlowEvent<T> {
        fun asFloat() : Float {
            val progressFraction = progress.toFloat() / size
            return min(1.0f, max(0.0f, progressFraction))
        }
    }

    data class Success<T>(
        val data: T
    ) : FlowEvent<T>
}