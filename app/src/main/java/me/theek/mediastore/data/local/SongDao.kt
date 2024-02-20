package me.theek.mediastore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.theek.mediastore.data.local.entity.SongEntity

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongs(songEntity: SongEntity)

    @Query("SELECT * FROM songs")
    fun getAllSongs() : Flow<List<SongEntity>>
}