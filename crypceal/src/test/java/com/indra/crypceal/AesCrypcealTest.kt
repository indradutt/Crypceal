package com.indra.crypceal

import android.content.Context
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.nio.charset.Charset
import kotlin.reflect.KClass

@RunWith(RobolectricTestRunner::class)
class AesCrypcealTest {
    private lateinit var crypceal : Crypceal
    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.systemContext
        crypceal = Crypceal(context, Crypceal.TYPE.AES)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun testPositiveResults() {
        val input = "PasswordToTest"
        val encrypted = crypceal.encrypt(input.toByteArray(Charsets.UTF_8))
        val output = crypceal.decrypt(encrypted)
        assertEquals(input, output)
    }

    fun testWhenInputIsNull() {

    }

    fun testForApi21() {

    }
}