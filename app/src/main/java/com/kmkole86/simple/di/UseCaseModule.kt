package com.kmkole86.simple.di

import com.kmkole86.data.di.CacheFirst
import com.kmkole86.data.di.IoDispatcher
import com.kmkole86.domain.repository.MovieRepository
import com.kmkole86.domain.use_case.GetPageUseCase
import com.kmkole86.domain.use_case.ObserveCachedPageRangeUseCase
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Reusable
    fun provideGetPageUseCase(
        @IoDispatcher dispatcher: CoroutineDispatcher,
        @CacheFirst repository: MovieRepository
    ): GetPageUseCase = GetPageUseCase(dispatcher, repository)

    @Provides
    @Reusable
    fun provideObserveCachedPageRangeUseCase(
        @IoDispatcher dispatcher: CoroutineDispatcher,
        @CacheFirst repository: MovieRepository
    ): ObserveCachedPageRangeUseCase = ObserveCachedPageRangeUseCase(dispatcher, repository)
}