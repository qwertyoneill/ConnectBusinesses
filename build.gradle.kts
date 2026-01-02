// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle.kts (nível de projeto)

plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false // ADICIONAR ISTO
    id("com.google.dagger.hilt.android") version "2.51.1"  apply false
    // ✅ Firebase (FORMA MODERNA)
    id("com.google.gms.google-services") version "4.4.4" apply false
}
