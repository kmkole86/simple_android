package com.kmkole86.data.remote.di

import com.kmkole86.data.data_source.MovieRemoteDataSource
import com.kmkole86.data.remote.data_source_impl.MovieRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDataSourceModule {

    @Binds
    @Singleton
    fun provideMovieRemoteDataSource(dataSource: MovieRemoteDataSourceImpl): MovieRemoteDataSource
}