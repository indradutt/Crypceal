package com.indra.crypceal

import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import java.security.KeyStore
import java.security.KeyStoreException


@Implements(KeyStore::class)
object ShadowKeyStore {
    @Implementation
    @Throws(KeyStoreException::class)
    fun getInstance(type: String): KeyStore {
        throw KeyStoreException("SHADOW WORKING")
    }
}