


plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.solutionsproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.solutionsproject"
        minSdk = 27
        targetSdk = 34
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
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    androidTestImplementation(libs.core)
    androidTestImplementation(libs.junit.v110)

    implementation(libs.glide)
    implementation(project(":are"))

    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)

    // START_OF[Retrofit Dependencies]
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    // END_OF[Retrofit Dependencies]

    // START_OF[Picasso Dependencies]
    implementation(libs.picasso)
    // END_OF[Picasso Dependencies]

    implementation(libs.lifecycle.livedata.ktx.v270)
    implementation(libs.lifecycle.viewmodel.ktx.v270)

    // START_OF[JWT]
    implementation(libs.java.jwt)
    // END_OF[JWT]

    implementation(libs.coil)

    implementation(libs.recyclerview)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.fragment)
    implementation(libs.activity)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}