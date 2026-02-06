plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    // id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.vietnameselearning"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vietnameselearning"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    // For Kotlin projects using KSP
    ksp {

        arg("room.schemaLocation", "$projectDir/schemas")

    }



    buildTypes {

        release {

            isMinifyEnabled = false

            isDebuggable = false

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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }


    buildFeatures {
        compose = true
    }
    androidResources {
        generateLocaleConfig = true
    }


    // Added
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    // --------------------------

}

configurations {

    create("cleanedAnnotations")

    implementation {

//        //exclude(group = "org.jetbrains", module = "annotations")

        exclude(group = "com.intellij", module = "annotations")

    }

}




dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.material3)

    implementation(libs.androidx.navigation.runtime.ktx)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.navigation.testing)

    implementation(libs.core.ktx)

    implementation(libs.androidx.compose.ui.test.junit4)

    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.compose.ui)

    testImplementation(libs.junit)

    testImplementation(libs.robolectric)

    debugImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)




    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.room.ktx)



    implementation(libs.kotlinx.serialization.json)



    // Retrofit & GSON Converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // datastore
    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    // Alternatively - without an Android dependency.
    implementation("androidx.datastore:datastore-preferences-core:1.2.0")

    implementation(libs.androidx.media3.exoplayer)




}

