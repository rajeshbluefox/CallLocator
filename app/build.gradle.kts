plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.gms.google-services")

    kotlin("kapt")
}


android {
    namespace = "com.familylocation.mobiletracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.familylocation.mobiletracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 23
        versionName = "1.23"

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
    useLibrary("org.apache.http.legacy")


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        dataBinding=true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.hilt:hilt-common:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")



    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")

    val lifecycleVersion = "2.5.1"
    val archVersion = "2.2.0-alpha01"
    val retrofit2Version = "2.9.0"
    val okhttp3Version = "4.9.0"
    val coroutineVersion = "1.4.2"
    val gsonVersion = "2.8.6"

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:$archVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$archVersion")

    // Retrofit and OkHttp
    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.google.code.gson:gson:$gsonVersion")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

    // ViewModel (Note: androidx.lifecycle:extensions is deprecated, use androidx.lifecycle:lifecycle-viewmodel-ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    val lottieVersion = "3.4.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")

    implementation("com.facebook.shimmer:shimmer:0.1.0@aar")

    //Google Maps
    implementation("com.google.android.gms:play-services-maps:17.0.1")
    implementation("com.google.android.gms:play-services-location:17.1.0")

    implementation("com.squareup.moshi:moshi:1.12.0")

    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    //Compass
    implementation("com.github.arbelkilani:Compass-View:v1.1.1")

    //Google Ads
    implementation("com.google.android.gms:play-services-ads:23.1.0")
    implementation("androidx.multidex:multidex:2.0.1")

    //GDPR
    implementation("com.google.android.ump:user-messaging-platform:2.2.0")


    //Mediation
    implementation("com.google.ads.mediation:applovin:12.4.3.0")
    implementation ("com.google.ads.mediation:facebook:6.17.0.0")

    implementation("com.google.ads.mediation:mintegral:16.7.21.0")

    //OneSignal
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

    //CountryCode
    implementation("com.hbb20:ccp:2.6.0")

    //Number Validator
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.38")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Add the dependency for the Analytics library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-analytics")

    //Room DataBase
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("com.github.ibrahimsn98:live-preferences:1.9")



}