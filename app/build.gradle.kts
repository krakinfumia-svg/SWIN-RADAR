
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.swin.radar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.swin.radar"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4"
    )
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )
}

