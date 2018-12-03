package com.indra.crypceal

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.indra.crypceal.Crypceal.TYPE.*
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.security.auth.x500.X500Principal


class Crypceal(private val context: Context, private val type: TYPE) {
    private var handler: EncryptionHandler
    private var keyStore: KeyStore? = null

    enum class TYPE {
        DEFAULT, //RSA + AES
        AES,
        RSA
    }

    companion object {
        private const val TAG  = "Crypceal"
        private const val ALIAS_AES = "Crypceal_Aes"
        private const val ALIAS_RSA = "Crypceal_Rsa"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    init {
        initKeyStore()
        initKeyStoreForAES()
        initKeyStoreForRSA()
        handler = when(type) {
            DEFAULT -> AesRsaCrypceal()
            AES -> AesCrypceal()
            RSA -> RsaCrypceal()
        }
    }

    fun encrypt(plainText: ByteArray): ByteArray {
        when(keyStore?.containsAlias(ALIAS_AES)){
            true -> Log.d(TAG, "Key is present")
            else ->
                Log.d(TAG, "Key is not there")
        }

        val key = keyStore?.getKey(ALIAS_AES, null) as SecretKey
        return handler.encrypt(plainText, key)
    }

    fun decrypt(encryptedData: ByteArray): ByteArray {
        val key = keyStore?.getKey(ALIAS_AES, null) as SecretKey
        return handler.decrypt(encryptedData, key)
    }

    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore?.load(null)
    }

    private fun initKeyStoreForAES() {
        if (type != AES) return

        if (keyStore?.containsAlias(ALIAS_AES)!!) {
            if (BuildConfig.DEBUG) Log.d(TAG, "AES Key is already present, no need to setup")
            return
        }

        if (BuildConfig.DEBUG) Log.d(TAG, "Generating a key and storing it")

        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val spec = KeyGenParameterSpec.Builder(
                    ALIAS_AES, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .build()

            keyGen.init(spec)
            keyGen.generateKey()
        } else {
            throw Exception("AES encryption is only supported on API 23 or higher. Please choose DEFAULT/RSA type.")
        }
    }

    private fun initKeyStoreForRSA() {
        if (type == AES) return

        if (keyStore?.containsAlias(ALIAS_RSA)!!) {
            if (BuildConfig.DEBUG)Log.d(TAG, "RSA Key is already present, no need to setup")
            return
        }
        if (BuildConfig.DEBUG) Log.d(TAG, "Setting up RSA keys for android-${Build.VERSION.SDK_INT}")
        val start = Calendar.getInstance(TimeZone.getTimeZone("PST"))
        val end = Calendar.getInstance(TimeZone.getTimeZone("PST"))
        end.add(Calendar.MINUTE, 1)

        val kpGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val spec = KeyGenParameterSpec.Builder(
                    ALIAS_RSA, KeyProperties.PURPOSE_DECRYPT)
                    .setCertificateSubject(X500Principal("CN=$ALIAS_RSA"))
                    .setCertificateSerialNumber(BigInteger.TEN)
                    .setCertificateNotBefore(start.time)
                    .setCertificateNotAfter(end.time)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setKeySize(1024)
                    .build()
            kpGen.initialize(spec)
            kpGen.generateKeyPair()
        } else {
            val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(ALIAS_RSA)
                    .setSubject(X500Principal("CN=$ALIAS_RSA"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .setKeySize(1024)
                    .build()
            kpGen.initialize(spec)
            kpGen.generateKeyPair()
        }
    }
}