package com.indra.crypceal

import java.security.Key
import javax.crypto.Cipher

class RsaCrypceal : EncryptionHandler {
    companion object {
        private val TAG = "RsaCrypceal"
        private val transformation = "RSA/ECB/PKCS1Padding"
        private val DECRYPTION_BLOCK_SIZE = 128
        private val RESERVED_PADDING_SIZE = 11
        private val ENCRYPTION_BLOCK_SIZE = DECRYPTION_BLOCK_SIZE - RESERVED_PADDING_SIZE
    }

    override fun encrypt(input: ByteArray, key: Key): ByteArray {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return doFinal(cipher, input, Cipher.ENCRYPT_MODE)
    }

    override fun decrypt(encrypted: ByteArray, key: Key): ByteArray {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return doFinal(cipher, encrypted, Cipher.DECRYPT_MODE)
    }

    private fun doFinal(cipher: Cipher, input: ByteArray, mode: Int) : ByteArray {
        var remainingBytesLength = input.size
        var startIndex = 0
        var chunkSize = 0

        var result = ByteArray(0)

        var resBlockSize = when (mode == Cipher.ENCRYPT_MODE) {
            true -> ENCRYPTION_BLOCK_SIZE
            else -> DECRYPTION_BLOCK_SIZE
        }

        while (remainingBytesLength > 0) {
            chunkSize = if (remainingBytesLength > resBlockSize) resBlockSize else remainingBytesLength

            var buffer = input.copyOfRange(startIndex, chunkSize)
            var chunk = cipher.doFinal(buffer)
            result += chunk

            startIndex += chunkSize
            remainingBytesLength -= chunkSize
        }

        return result
    }
}