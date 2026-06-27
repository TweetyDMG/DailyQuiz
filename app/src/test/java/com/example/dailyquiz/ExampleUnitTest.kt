package com.example.dailyquiz

import com.example.dailyquiz.util.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ResourceTest {

    @Test
    fun `success holds data`() {
        val resource = Resource.Success(listOf("a", "b"))
        assertEquals(listOf("a", "b"), resource.data)
        assertNull(resource.message)
    }

    @Test
    fun `error holds message and optional data`() {
        val resource = Resource.Error<String>("Something went wrong")
        assertEquals("Something went wrong", resource.message)
        assertNull(resource.data)
    }

    @Test
    fun `error with data`() {
        val resource = Resource.Error("error", listOf("a"))
        assertEquals(listOf("a"), resource.data)
        assertEquals("error", resource.message)
    }

    @Test
    fun `success data is accessible`() {
        val resource = Resource.Success(42)
        assertNotNull(resource.data)
        assertEquals(42, resource.data)
    }
}
