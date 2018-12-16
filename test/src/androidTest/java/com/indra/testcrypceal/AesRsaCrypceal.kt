package com.indra.testcrypceal

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.indra.crypceal.Crypceal
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AesRsaCrypceal {
    private lateinit var crypceal: Crypceal

    @Before
    fun setUp() {
        crypceal = Crypceal(InstrumentationRegistry.getTargetContext())
    }

    @Test
    fun testForPositiveResults() {
        val input = "The Android Plugin for Gradle compiles the instrumented test code located in the default directory "
        val encrypted = crypceal.encrypt(input.toByteArray(Charsets.UTF_8))
        val output = crypceal.decrypt(encrypted)
        Assert.assertEquals(input, String(output))
    }
}