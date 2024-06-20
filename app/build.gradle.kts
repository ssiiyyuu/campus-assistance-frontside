plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.siyu.campus_assistance_frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.siyu.campus_assistance_frontend"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.okhttp)
    implementation(libs.fastjson)
    implementation(libs.hutool.all)
    implementation(libs.lombok)
    // PictureSelector
    implementation(libs.pictureselector)
    // DateTimePicker
    implementation(libs.material.v110)
    implementation(libs.dateTimePicker)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    annotationProcessor(libs.lombok)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}