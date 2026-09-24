package com.example.alphabetlauncher

import android.content.Context
import android.content.Intent

object AppLauncher {
    fun launch(context: Context, app: AppInfo) {
        val intent = context.packageManager.getLaunchIntentForPackage(app.packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
