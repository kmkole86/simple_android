package com.kmkole86.data.remote.di

import com.kmkole86.data.data_source.MovieRemoteDataSource
import com.kmkole86.data.di.PageOrdinalWrapper
import com.kmkole86.data.di.RemoteDataSource
import com.kmkole86.data.remote.data_source_impl.MovieRemoteDataSourceImpl
import com.kmkole86.data.remote.data_source_impl.PageOrdinalsWrapper
import com.kmkole86.data.remote.utils.Constants.HTTP_TIME_OUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient = HttpClient(Android) {

        engine {
            connectTimeout = HTTP_TIME_OUT
            socketTimeout = HTTP_TIME_OUT
        }

        install(DefaultRequest) {
            url {
                url("https://api.themoviedb.org/3/movie/")
                parameters.append("api_key", "a794ee27f47722d30bc1c67e3df3522a")
            }
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }


    @Provides
    @PageOrdinalWrapper
    fun providePageOrdinalsWrapper(repository: PageOrdinalsWrapper): MovieRemoteDataSource {
        return repository
    }

    @Provides
    @RemoteDataSource
    fun provideMovieRemoteDataSourceImpl(repository: MovieRemoteDataSourceImpl): MovieRemoteDataSource {
        return repository
    }
}