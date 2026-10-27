import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// AGP 9 provides built-in Kotlin support; do not apply org.jetbrains.kotlin.android.
android {
    namespace = "william.miranda.brutuscalendar.demo.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "william.miranda.brutuscalendar.demo.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = project.version.toString()
    }

    // Use the local debug key only to install the release demo for performance testing.
    buildTypes {
        getByName("release") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    implementation(project(":demo"))
    implementation(libs.androidx.activity.compose)
}
