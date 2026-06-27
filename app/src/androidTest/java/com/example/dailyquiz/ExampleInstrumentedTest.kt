package com.example.dailyquiz

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test verifying the application context is correct.
 */
@RunWith(AndroidJUnit4::class)
class ApplicationTest {

    @Test
    fun appContextIsCorrect() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.dailyquiz", appContext.packageName)
    }
}
