package com.indra.testcrypceal

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.indra.crypceal.Crypceal
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AesCrypcealTest {
    private lateinit var crypceal: Crypceal
    private lateinit var context: Context

    @Before
    fun setUp() {
        crypceal = Crypceal(InstrumentationRegistry.getTargetContext(), Crypceal.TYPE.AES)
    }

    @Test
    fun testForPositiveResults() {
        val input = "The Android Plugin for Gradle compiles the instrumented test code located in the default directory "
        val encrypted = crypceal.encrypt(input.toByteArray(Charsets.UTF_8))
        val output = crypceal.decrypt(encrypted)
        Assert.assertEquals(input, String(output))
    }
}