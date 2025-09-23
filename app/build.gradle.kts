    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.jetbrains.kotlin.android)
        alias(libs.plugins.google.gms.google.services)
        id("com.google.devtools.ksp") version "1.9.20-1.0.14"
        //    kotlin("kapt")
    }

    android {
        namespace = "app.perkhub"
        compileSdk = 35

        defaultConfig {
            applicationId = "app.perkhub"
            minSdk = 26
            targetSdk = 35
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
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }

        buildFeatures {
            viewBinding = true
        }
    }
    dependencies {
        implementation(libs.androidx.core)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.gson)
        implementation(libs.androidx.retrofit)
        implementation(libs.androidx.retrofit.gson)
        implementation(libs.androidx.okhttp)
        implementation(libs.firebase.auth)
        implementation(libs.androidx.glide)
        implementation(libs.androidx.browser)
        implementation(libs.firebase.messaging)
        implementation(libs.androidx.lottie)

        // Room
    //    kapt(libs.androidx.room.compiler)
        ksp(libs.androidx.room.compiler)  // Changed from kapt to ksp

        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)

        // Lifecycle
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)

        // Coroutines
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.coroutines.android)
        // Test dependencies
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }


    /*
    dependencies {
        implementation(libs.androidx.core)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.gson)
        implementation(libs.androidx.retrofit)
        implementation(libs.androidx.retrofit.gson)
        implementation(libs.androidx.okhttp)
        implementation(libs.androidx.lifecycle.viewmodel.android)
        implementation(libs.firebase.auth)
        implementation(libs.androidx.glide)
        implementation(libs.androidx.browser)
        implementation(libs.firebase.messaging)
        implementation(libs.androidx.lottie)
        implementation(libs.androidx.room.compiler)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.coroutines.android)


    }

     */