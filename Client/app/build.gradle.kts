


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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    androidTestImplementation("androidx.test:core:1.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.0")

    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation(project(":are"))

    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)

    // START_OF[Retrofit Dependencies]
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // END_OF[Retrofit Dependencies]

    // START_OF[Picasso Dependencies]
    implementation(libs.picasso)
    // END_OF[Picasso Dependencies]

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // START_OF[JWT]
    implementation("com.auth0:java-jwt:4.4.0")
    // END_OF[JWT]

    implementation("io.coil-kt:coil:2.6.0")

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