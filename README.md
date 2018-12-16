Crypceal
=======

A general purpose library for encyption user data

Description
--------
Crypceal provides AES/RSA/RSA+AES implementations to encrypt user data.

AES - as many of us already know that android started supporting AES key 
      stored in keystore API 23 onwards, this encryption is best suited
      to applications targeted for minSDKVersion 23. In case you try to
      use it for lower sdks, it will throw a RuntimeException.
```
      e.g. - Crypceal(context, Crypceal.TYPE.AES).encrypt(userData: ByteArray)
             Crypceal(context, Crypceal.TYPE.AES).decrypt(encryptedData: ByteArray)
```

RSA - this implementation is best suited for smaller chunk of data (128 bits),
      while this implemenation still capable of encrypting user data but it
      may be slow if the user data is large and computational performance is
      desired.
```
      e.g. - Crypceal(context, Crypceal.TYPE.RSA).encrypt(userData: ByteArray)
             Crypceal(context, Crypceal.TYPE.RSA).decrypt(encryptedData: ByteArray)
```

Default - this implementation uses AES keys to encrypt/decrypt user data and key resides
      inside app data storage in encrypted form, RSA asymmetric keys are used to 
      encrypt AES key. By default Crypceal object uses this implemenatation.
```
      e.g. - Crypceal(context).encrypt(userData: ByteArray)
             Crypceal(context).decrypt(encryptedData: ByteArray)
```
