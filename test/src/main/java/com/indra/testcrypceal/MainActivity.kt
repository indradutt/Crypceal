package com.indra.testcrypceal

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.indra.crypceal.Crypceal
import indra.com.testcrypceal.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate..")
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            testPositiveResultsForRSA()
        }
    }

    private fun testPositiveResultsForAES() {
        val input = "PasswordToTest"
        val crypceal = Crypceal(applicationContext, Crypceal.TYPE.AES)
        val encrypted = crypceal.encrypt(input.toByteArray(Charsets.UTF_8))
        val output = crypceal.decrypt(encrypted)
        Log.d(TAG, "output: "+ String(output))
    }

    private fun testPositiveResultsForRSA() {
        val input = "NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException"
        val crypceal = Crypceal(applicationContext, Crypceal.TYPE.RSA)
        val encrypted = crypceal.encrypt(input.toByteArray(Charsets.UTF_8))
        val output = crypceal.decrypt(encrypted)
        Log.d(TAG, "output: "+ String(output))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
