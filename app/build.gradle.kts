plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "com.nusachat.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nusachat.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }
}

dependencies {

    // Signal Protocol (libsignal-android) - End-to-end encryption
    // NOTE: libsignal requires core library desugaring which has a known
    // incompatibility with AGP 8.9 + Gradle 9.7.1 (D8 "global synthetic for
    // Record desugaring" error). To build with libsignal, upgrade toolchain
    // to AGP 8.4+ with compatible Gradle version, then uncomment:
    //
    // implementation("org.signal:libsignal-android:0.86.5")
    // coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    // and enable isCoreLibraryDesugaringEnabled = true in compileOptions
    //
    // All Signal code files are present in src/signal/ but excluded from compilation.

    // Compose BOM (latest stable)
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Lifecycle ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Firebase BoM + Firebase Auth (Phone Number Verification)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth")

    // SQLCipher for local database encryption
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")

    // Ktor client for WebSocket (lightweight)
    implementation("io.ktor:ktor-client-core:2.3.10")
    implementation("io.ktor:ktor-client-okhttp:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")

    // Coil Compose for avatar loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // DataStore Preferences untuk penyimpanan lokal terenkripsi
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.sqlite:sqlite:2.4.0")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.0")
}