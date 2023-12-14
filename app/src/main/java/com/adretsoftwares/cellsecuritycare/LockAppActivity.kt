package com.adretsoftwares.cellsecuritycare

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adretsoftwares.cellsecuritycare.adapter.AppsDetailsAdapter
import com.adretsoftwares.cellsecuritycare.databinding.ActivityLockAppBinding
import com.adretsoftwares.cellsecuritycare.model.AppDetails

class LockAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockAppBinding
    private var apps: List<AppDetails> = emptyList()
    private val adapter = AppsDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInstalledApps()
        binding.appsList.adapter = adapter
    }

    private fun loadInstalledApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        apps = packageManager.queryIntentActivities(
            mainIntent,
            PackageManager.MATCH_ALL
        ).map {
            val activityInfo = it.activityInfo
            AppDetails(
                label = activityInfo.loadLabel(packageManager).toString(),
                packageName = activityInfo.packageName,
                isHidden = false,
                icon = activityInfo.loadIcon(packageManager)
            )
        }
        adapter.submitList(apps)
    }
}