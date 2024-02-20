package me.theek.mediastore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import me.theek.mediastore.data.local.entity.SongEntity

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao() : SongDao
}