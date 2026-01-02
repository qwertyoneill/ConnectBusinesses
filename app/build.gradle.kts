plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tiagovaz.connectbusinesses"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tiagovaz.connectbusinesses"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }


    kotlinOptions { jvmTarget = "17" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kapt {
        useBuildCache = true
        javacOptions {
            option("-Xjdk-release", "17")
        }
    }
}

dependencies {
    // Compose BOM – usa algo recente e estável
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil:2.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    implementation("androidx.compose.foundation:foundation:1.7.4")

    // Credential Manager (MODERNO)
    implementation("androidx.credentials:credentials:1.6.0-rc01")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-rc01")

    // Google Identity (MODERNO)
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")


    // 🔥 Firebase BOM (2025)
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))

    // 🔐 Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //accompanist-swiperefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.31.5-beta")
}
