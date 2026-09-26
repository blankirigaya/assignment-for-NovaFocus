plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

import com.android.build.gradle.AppExtension
import java.io.File

tasks.register("installLauncher") {
    dependsOn("installDebug")
    val androidExt = project.extensions.getByType<AppExtension>()
    val suffix = if (System.getProperty("os.name").startsWith("Windows")) ".exe" else ""
    val adb = androidExt.sdkDirectory.resolve("platform-tools/adb$suffix").absolutePath
    val appId = androidExt.defaultConfig.applicationId
    doLast {
        require(File(adb).exists()) { "adb not found at $adb" }
        fun runAdb(args: List<String>): String {
            val process = ProcessBuilder(listOf(adb) + args).redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readText().trim()
            check(process.waitFor() == 0) { "adb ${args.joinToString(" ")} failed: $output" }
            return output
        }
        val serial = System.getenv("ANDROID_SERIAL").orEmpty()
        val target = if (serial.isNotEmpty()) listOf("-s", serial) else emptyList()
        if (serial.isEmpty()) {
            val ready = runAdb(listOf("devices")).lines().drop(1).mapNotNull { line: String ->
                line.trim().split("\\s+".toRegex()).takeIf { parts: List<String> -> parts.size >= 2 }
            }.filter { parts: List<String> -> parts[1] == "device" }
            require(ready.size == 1) { "Expected exactly 1 ready device, found ${ready.size}. Set ANDROID_SERIAL to target one." }
        }
        val id = appId ?: throw GradleException("applicationId missing")
        runAdb(target + listOf("shell", "cmd", "package", "set-home-activity", "$id/.MainActivity"))
        println("Default HOME set to $id/.MainActivity")
    }
}

android {
    namespace = "com.example.alphabetlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.alphabetlauncher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
}
