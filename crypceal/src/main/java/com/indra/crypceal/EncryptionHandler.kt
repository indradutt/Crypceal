package com.indra.crypceal

import java.security.Key

interface EncryptionHandler {
    fun encrypt(plainText: ByteArray, key: Key) : ByteArray
    fun decrypt(encrypted: ByteArray, key: Key) : ByteArray
}