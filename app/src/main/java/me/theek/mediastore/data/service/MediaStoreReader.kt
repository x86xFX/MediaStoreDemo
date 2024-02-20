package me.theek.mediastore.data.service

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.simplecityapps.ktaglib.KTagLib
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import me.theek.mediastore.domain.model.Song
import me.theek.mediastore.domain.repository.FlowEvent
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreReader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val kTagLib: KTagLib
) {

    fun getSongs(): Flow<FlowEvent<List<Song>>> = flow {

        Log.d("MediaStoreReader", "Flow executed on ${Thread.currentThread().name}")

        val songs: MutableList<Song> = mutableListOf()

        val songCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
            ),
            "${MediaStore.Audio.Media.IS_MUSIC} = ?",
            arrayOf("1"),
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        songCursor?.use { cursor ->
            var progress = 0
            var tempID = 0

            while (currentCoroutineContext().isActive && cursor.moveToNext()) {
                val song = Song(
                    id = tempID,
                    songName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                    artistName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                    albumName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                    duration = cursor.getIntOrNull(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                )

                songs.add(song)
                progress++
                emit(
                    FlowEvent.Progress(
                        size = songCursor.count,
                        progress = progress,
                        message = "${song.songName} • ${song.artistName}"
                    )
                )
                tempID++
            }

            emit(FlowEvent.Success(songs))
        }
    }

    @WorkerThread
    suspend fun getSongCover(path: String): ByteArray? = withContext(Dispatchers.IO) {
        val uri: Uri =
            if (path.startsWith("content://")) {
                Uri.parse(path)
            } else {
                Uri.fromFile(File(path))
            }

        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                kTagLib.getArtwork(pfd.detachFd())
            }
        } catch (e: Exception) {
            null
        }
    }
}