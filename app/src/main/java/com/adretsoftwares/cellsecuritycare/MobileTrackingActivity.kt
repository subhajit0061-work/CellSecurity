package com.adretsoftwares.cellsecuritycare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.adretsoftwares.cellsecuritycare.databinding.ActivityMobileTrackingBinding
import com.adretsoftwares.cellsecuritycare.services.MobileTrackingService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MobileTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileTrackingBinding
    private var smsJob: Job? = null
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