plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> origin/main
=======

>>>>>>> origin/main
}

android {
    namespace = "com.example.munchly"
<<<<<<< HEAD
<<<<<<< HEAD
    compileSdk = 34
=======
    compileSdk {
        version = release(36)
    }
>>>>>>> origin/main
=======
    compileSdk {
        version = release(36)
    }
>>>>>>> origin/main

    defaultConfig {
        applicationId = "com.example.munchly"
        minSdk = 26
<<<<<<< HEAD
<<<<<<< HEAD
        targetSdk = 34
=======
        targetSdk = 36
>>>>>>> origin/main
=======
        targetSdk = 36
>>>>>>> origin/main
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
<<<<<<< HEAD
<<<<<<< HEAD
    // Core Android & Compose (keep existing libs)
=======
>>>>>>> origin/main
=======
>>>>>>> origin/main
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
<<<<<<< HEAD
<<<<<<< HEAD

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Testing
=======
>>>>>>> origin/main
=======
>>>>>>> origin/main
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
<<<<<<< HEAD
<<<<<<< HEAD

    implementation("androidx.compose.material:material-icons-extended:1.5.4")
=======
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
>>>>>>> origin/main
=======
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
>>>>>>> origin/main
}