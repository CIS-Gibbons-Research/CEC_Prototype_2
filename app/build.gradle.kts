plugins {
    id("com.android.application")
}

android {
    namespace = "css.cecprototype2"
    compileSdk = 34

    defaultConfig {
        applicationId = "css.cecprototype2"
        minSdk = 28
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
    buildFeatures {
        dataBinding = true
        viewBinding =  true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment:2.7.4")
    implementation("androidx.navigation:navigation-ui:2.7.4")

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.2.3"
    //val camerax_version = "1.3.0-rc02"

    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    //implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    //implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    //implementation("androidx.camera:camera-extensions:${camerax_version}")

    // needed for Simple Linear Regression
    implementation ("org.apache.commons:commons-math3:3.6.1")

    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")

    constraints{
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0")
    }

    // Add for gson library for parsing json files
    //implementation ("com.google.code.gson:gson:2.10.1")
    // Add for Volley libary for HTTP requests, see https://developer.android.com/training/volley
    implementation("com.android.volley:volley:1.2.1")

}