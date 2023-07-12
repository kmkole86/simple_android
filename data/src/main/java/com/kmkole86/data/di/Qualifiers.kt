package com.kmkole86.data.di

import javax.inject.Qualifier

@Qualifier
annotation class DefaultDispatcher

@Qualifier
annotation class IoDispatcher

@Qualifier
annotation class MainImmediateDispatcher

@Qualifier
annotation class CacheFirst

@Qualifier
annotation class CacheResult

@Qualifier
annotation class FetchMissingPages

@Qualifier
annotation class MovieRepo

@Qualifier
annotation class RemoteDataSource

@Qualifier
annotation class PageOrdinalWrapper




