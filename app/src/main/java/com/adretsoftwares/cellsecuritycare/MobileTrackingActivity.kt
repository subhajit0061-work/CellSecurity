package com.adretsoftwares.cellsecuritycare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adretsoftwares.cellsecuritycare.databinding.ActivityMobileTrackingBinding
import com.adretsoftwares.cellsecuritycare.services.MobileTrackingService

class MobileTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileTrackingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.num1Switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                stopService(Intent(this, MobileTrackingService::class.java))
                startService(Intent(this, MobileTrackingService::class.java))
            } else
                stopService(Intent(this, MobileTrackingService::class.java))
        }
    }
}