package com.example.alphabetlauncher

import android.content.Context
import android.content.Intent

object AppRepository {
    fun load(context: Context): Pair<List<AppInfo>, Map<Char, List<AppInfo>>> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val infos = pm.queryIntentActivities(intent, 0).mapNotNull { resolve ->
            val name = resolve.loadLabel(pm)?.toString()?.trim().orEmpty()
            if (name.isEmpty()) return@mapNotNull null
            val packageName = resolve.activityInfo?.packageName ?: return@mapNotNull null
            val first = name.first().uppercaseChar()
            val letter = if (first in 'A'..'Z') first else '#'
            AppInfo(name, packageName, resolve.loadIcon(pm), letter)
        }.sortedBy { it.name.lowercase() }.distinctBy { it.packageName }
        return infos to infos.groupBy { it.letter }
    }
}
