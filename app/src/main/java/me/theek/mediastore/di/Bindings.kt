package me.theek.mediastore.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.theek.mediastore.data.repository.LocalSongRepository
import me.theek.mediastore.domain.repository.SongRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class Bindings {

    @Singleton
    @Binds
    abstract fun bindsSongRepository(localSongRepository: LocalSongRepository) : SongRepository
}