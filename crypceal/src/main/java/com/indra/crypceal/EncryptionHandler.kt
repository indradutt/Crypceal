package com.indra.crypceal

import javax.crypto.SecretKey

interface EncryptionHandler {
    fun encrypt(plainText: ByteArray, key: SecretKey) : ByteArray
    fun decrypt(encrypted: ByteArray, key: SecretKey) : ByteArray
}