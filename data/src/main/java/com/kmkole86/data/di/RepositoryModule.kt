package com.kmkole86.data.di

import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.data.data_source.MovieRemoteDataSource
import com.kmkole86.data.repository_impl.CacheFirstRepository
import com.kmkole86.data.repository_impl.CacheResultRepository
import com.kmkole86.data.repository_impl.FetchMissingPagesRepository
import com.kmkole86.data.repository_impl.MovieRepositoryImpl
import com.kmkole86.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     *Offline first behaviour and Caching values behaviour is
     * placed into separated layers ("wrappers") around the MovieRepository
     *
     * CachedProxy => CachingDecorator => MovieRepository
     *
     * CachedProxy Offline first, checks if value is cached,
     *  if true returns from cache, otherwise pass the call to the next layer
     *
     * CachingDecorator => After the page is fetched it will cache it
     *
     * MovieRepository => Regular repository works with remote and local datasource
     */

    @Provides
    @Singleton
    @CacheFirst
    fun provideCacheFirstRepository(
        localDataSource: MovieLocalDataSource,
        @CacheResult repository: MovieRepository
    ): MovieRepository {
        return CacheFirstRepository(
            localDataSource = localDataSource,
            repository = CacheResultRepository(
                localDataSource = localDataSource,
                repository = repository
            )
        )
    }

    @Provides
    @Singleton
    @CacheResult
    fun provideCacheResultRepository(
        localDataSource: MovieLocalDataSource,
        @FetchMissingPages repository: MovieRepository
    ): MovieRepository {
        return CacheResultRepository(
            localDataSource = localDataSource,
            repository = CacheResultRepository(
                localDataSource = localDataSource,
                repository = repository
            )
        )
    }

    @Provides
    @Singleton
    @FetchMissingPages
    fun provideFetchMissingPageRepository(
        localDataSource: MovieLocalDataSource,
        @MovieRepo repository: MovieRepository
    ): MovieRepository {
        return FetchMissingPagesRepository(
            localDataSource = localDataSource,
            repository = CacheResultRepository(
                localDataSource = localDataSource,
                repository = repository
            )
        )
    }

    @Provides
    @Singleton
    @MovieRepo
    fun provideMovieRepository(
        localDataSource: MovieLocalDataSource,
        @PageOrdinalWrapper remoteDataSource: MovieRemoteDataSource
    ): MovieRepository {
        return MovieRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }
}