plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
    id("jacoco")
    kotlin("plugin.serialization")
}

android {
    namespace = "dev.piotrp.melodyshare"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.piotrp.melodyshare"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.firebase:firebase-installations-ktx:17.2.0")
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.20")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.ajalt:timberkt:1.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.14.2")
    implementation("com.github.LeffelMania:android-midi-lib:f8f2a6645")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

ktlint {
    android.set(true)
    outputColorName.set("RED")
}
