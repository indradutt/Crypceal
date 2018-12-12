package com.indra.crypceal

import android.util.Log
import java.security.Key
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class AesCrypceal : EncryptionHandler {
    companion object {
        private const val TAG = "AesCrypceal"
        private const val transformation = "AES/CBC/PKCS7Padding"
        private const val IV_LENGTH = 16
    }
    override fun encrypt(plainText: ByteArray, key: Key): ByteArray {
        if (BuildConfig.DEBUG) Log.d(TAG, "encrypting....")
        val cipher = Cipher.getInstance(transformation)
        var ivBytes = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(ivBytes)

        if (BuildConfig.DEBUG) Log.d(TAG, "SecretKey $key")

        cipher.init(Cipher.ENCRYPT_MODE, key)
        ivBytes = cipher.iv

        if(BuildConfig.DEBUG) Log.d(TAG, "encryption IV: "+ Arrays.toString(ivBytes))

        val encrypted = cipher.doFinal(plainText)

        return ivBytes + encrypted
    }

    override fun decrypt(encrypted: ByteArray, key: Key): ByteArray {
        val cipher = Cipher.getInstance(transformation)

        // Split to initialization vector and significant bytes to be decrypted.
        val ivBytes = encrypted.copyOfRange(0, IV_LENGTH)
        val encryptedTextBytes = encrypted.copyOfRange(IV_LENGTH, encrypted.size)

        if(BuildConfig.DEBUG) Log.d(TAG, "decryption IV: "+ Arrays.toString(ivBytes))

        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(ivBytes))

        return cipher.doFinal(encryptedTextBytes)
    }
}