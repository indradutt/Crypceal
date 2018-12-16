package com.indra.crypceal

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.indra.crypceal.Crypceal.TYPE.*
import java.math.BigInteger
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal


class Crypceal(private val context: Context, private val type: TYPE = DEFAULT) {
    private var handler: EncryptionHandler
    private var keyStore: KeyStore? = null

    /**
     *
     */
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
        private const val PREF_ALIAS = "crypceal_key"
    }

    init {
        initKeyStore()
        initKeyStoreForAES()
        initKeyStoreForRSA()
        initAESKeyForDefaultMode()
        handler = when(type) {
            DEFAULT -> AesCrypceal()
            AES -> AesCrypceal()
            RSA -> RsaCrypceal()
        }
    }

    /**
     * encrypts user data
     */
    fun encrypt(plainText: ByteArray): ByteArray {
        var key: Key
        key = when (type) {
            DEFAULT -> getAESKey()

            AES -> keyStore?.getKey(ALIAS_AES, null) as SecretKey

            RSA -> {
                val privateKey = keyStore?.getEntry(ALIAS_RSA, null) as KeyStore.PrivateKeyEntry
                privateKey.certificate.publicKey
            }
        }
        return handler.encrypt(plainText, key)
    }

    /**
     * decrypts encrypted data, use the same algorithm type which was used for encryption
     */
    fun decrypt(encryptedData: ByteArray): ByteArray {
        var key: Key
        key = when (type) {
            DEFAULT -> getAESKey()

            AES -> keyStore?.getKey(ALIAS_AES, null) as SecretKey

            RSA -> {
                val privateKey = keyStore?.getEntry(ALIAS_RSA, null) as KeyStore.PrivateKeyEntry
                privateKey.privateKey
            }
        }
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
            throw RuntimeException("AES encryption is only supported on API 23 or higher. Please choose DEFAULT/RSA type.")
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
        end.add(Calendar.YEAR, 25)

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

    private fun initAESKeyForDefaultMode() {
        if (type != DEFAULT) return
        val sharedPreferences = context.getSharedPreferences(PREF_ALIAS,
        Context.MODE_PRIVATE);
        //if not present, generate it, encrypt it and save it in shared preferences
        if(!sharedPreferences.contains(PREF_ALIAS)) {
            val keyGen = KeyGenerator.getInstance (KeyProperties.KEY_ALGORITHM_AES)
            keyGen.init(128)
            val secretKey = keyGen.generateKey()

            val encryptedSecretKey = encryptAESKey(secretKey.encoded)

            val editor = sharedPreferences.edit()
            editor.putString(PREF_ALIAS, encryptedSecretKey)
            editor.apply();
        }
    }

    private fun getAESKey() : Key {
        val sharedPreferences = context.getSharedPreferences(PREF_ALIAS,
                Context.MODE_PRIVATE)
        if (sharedPreferences.contains(PREF_ALIAS)) {
            val encryptedAesKey = sharedPreferences.getString(PREF_ALIAS, null)
            val decryptedAesKey = decryptAESKey(encryptedAesKey)
            return SecretKeySpec(decryptedAesKey, 0, decryptedAesKey.size, KeyProperties.KEY_ALGORITHM_AES)
        }
        throw RuntimeException("Could not find Key")
    }

    private fun encryptAESKey(aesKey: ByteArray) : String {
        val privateKey = keyStore?.getEntry(ALIAS_RSA, null)
                as KeyStore.PrivateKeyEntry
        val publicKey = privateKey.certificate.publicKey
        val encryptedData = RsaCrypceal().encrypt(aesKey, publicKey)
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
    }

    private fun decryptAESKey(encryptedAesKey: String) : ByteArray {
        val privateKey = keyStore?.getEntry(ALIAS_RSA, null)
                as KeyStore.PrivateKeyEntry
        val key = privateKey.privateKey as Key
        return RsaCrypceal().decrypt(Base64.decode(encryptedAesKey, Base64.NO_WRAP), key)
    }
}