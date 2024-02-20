package me.theek.mediastore.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.util.DebugLogger
import com.simplecityapps.ktaglib.KTagLib
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.theek.mediastore.data.local.SongDao
import me.theek.mediastore.data.local.SongDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context) : ImageLoader {
        return ImageLoader.Builder(context)
            .respectCacheHeaders(true)
            .logger(DebugLogger())
            .build()
    }

    @Provides
    @Singleton
    fun provideKTagLib() : KTagLib {
        return KTagLib()
    }

    @Provides
    @Singleton
    fun provideSongDatabase(@ApplicationContext context: Context) : SongDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = SongDatabase::class.java,
            name = "song.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(songDatabase: SongDatabase) : SongDao {
        return songDatabase.songDao()
    }
}