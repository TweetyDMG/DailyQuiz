package com.example.dailyquiz.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dailyquiz.data.model.QuizAttemptEntity
import com.example.dailyquiz.data.model.QuestionEntity

@Database(entities = [QuizAttemptEntity::class, QuestionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}