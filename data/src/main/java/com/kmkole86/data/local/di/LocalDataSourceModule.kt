package com.kmkole86.data.local.di

import android.app.Application
import androidx.room.Room
import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.data.local.PlainAndSimpleDatabase
import com.kmkole86.data.local.PlainAndSimpleDatabase.Companion.DATABASE_NAME
import com.kmkole86.data.local.data_source_impl.MovieLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun provideMovieLocalDataSource(dataSource: MovieLocalDataSourceImpl): MovieLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            context: Application
        ) = Room.databaseBuilder(
            context,
            PlainAndSimpleDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()

        @Provides
        @Singleton
        fun provideMovieDao(database: PlainAndSimpleDatabase) = database.moviesDao()
    }
}