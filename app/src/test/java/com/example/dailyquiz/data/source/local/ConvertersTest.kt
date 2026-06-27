package com.example.dailyquiz.data.source.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `string list to string and back`() {
        val original = listOf("Paris", "London", "Berlin")

        val serialized = converters.fromStringList(original)
        val deserialized = converters.toStringList(serialized)

        assertEquals(original, deserialized)
    }

    @Test
    fun `single element list`() {
        val original = listOf("JustOne")

        val serialized = converters.fromStringList(original)
        val deserialized = converters.toStringList(serialized)

        assertEquals(original, deserialized)
    }

    @Test
    fun `empty list returns empty string`() {
        val result = converters.fromStringList(emptyList())
        assertEquals("", result)
    }

    @Test
    fun `empty string deserializes to empty list`() {
        val result = converters.toStringList("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `list with special characters`() {
        val original = listOf("A & B", "C | D", "E ␟ F")

        val serialized = converters.fromStringList(original)
        val deserialized = converters.toStringList(serialized)

        assertEquals(original, deserialized)
    }
}
