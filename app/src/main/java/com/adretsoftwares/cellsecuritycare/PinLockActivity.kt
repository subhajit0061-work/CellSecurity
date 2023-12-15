package com.adretsoftwares.cellsecuritycare

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adretsoftwares.cellsecuritycare.SignupActivity.SHARED_PREF_NAME
import com.adretsoftwares.cellsecuritycare.common.Constants
import com.adretsoftwares.cellsecuritycare.databinding.ActivityPinLockBinding

class PinLockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinLockBinding
    private lateinit var preferences: SharedPreferences
    private var savedPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        savedPin = preferences.getString(Constants.SHARED_PIN, null)

        binding.btnEnter.setOnClickListener {
            checkPin()
        }
    }

    private fun checkPin() {
        val pin = binding.edtPin.text.toString()
        if (pin.length != 4 && pin != savedPin) {
            finish()
        } else {
            Toast.makeText(this, "Please check your pin", Toast.LENGTH_SHORT).show()
        }
    }
}