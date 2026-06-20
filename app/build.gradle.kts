import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
plugins {
    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    //id("org.jetbrains.kotlin.kapt")
    // ✅ Add this
    id("com.google.devtools.ksp")
}

android {

    namespace = "com.example.kotlinbasics"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.social.vitadrop"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Gemini API Key

        val apiKey =
            gradleLocalProperties(rootDir, providers)
                .getProperty("GEMINI_API_KEY")

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$apiKey\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }



    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose Activity
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI

    implementation("androidx.compose.animation:animation:1.5.0")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Material 3
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.foundation:foundation")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation (Needed for Login/Register navigation)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Lifecycle ViewModel for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    //google gson
    implementation("com.google.code.gson:gson:2.10.1")


    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.play.services.location)

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //KSP (modern)
    ksp("androidx.room:room-compiler:$room_version")

    // Personal AI Assistant
    implementation(
        "com.google.ai.client.generativeai:generativeai:0.9.0"
    )



    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}