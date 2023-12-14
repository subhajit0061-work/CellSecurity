package com.adretsoftwares.cellsecuritycare.model

import android.graphics.drawable.Drawable

data class AppDetails(
    val label: String,
    val icon: Drawable,
    val packageName: String,
    val isHidden: Boolean
)