package com.example.dailyquiz.di

import android.content.Context
import androidx.room.Room
import com.example.dailyquiz.data.repository.QuizRepositoryImpl
import com.example.dailyquiz.data.source.local.QuizDatabase
import com.example.dailyquiz.data.source.remote.ApiService
import com.example.dailyquiz.domain.repository.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizDatabase(@ApplicationContext context: Context): QuizDatabase {
        return Room.databaseBuilder(
            context,
            QuizDatabase::class.java,
            "quiz_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideQuizDao(database: QuizDatabase) = database.quizDao()

    @Provides
    @Singleton
    fun provideQuizRepository(
        apiService: ApiService,
        quizDao: com.example.dailyquiz.data.source.local.QuizDao
    ): QuizRepository {
        return QuizRepositoryImpl(apiService, quizDao)
    }
}