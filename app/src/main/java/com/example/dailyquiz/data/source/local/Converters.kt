package com.example.dailyquiz.data.source.local

import androidx.room.TypeConverter

class Converters {

    companion object {
        private const val DELIMITER = "␟"
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(DELIMITER)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        return value.split(DELIMITER)
    }
}