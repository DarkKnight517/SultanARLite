plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlinx-serialization")
}

android {
    namespace = "com.example.sultanarlite"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sultanarlite"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // 👇 ВСТАВЬ СЮДА
    packaging {
        resources {
            excludes += setOf(
                "META-INF/**",
                "kotlin/internal/internal.kotlin_builtins",
                "kotlin/reflect/reflect.kotlin_builtins",
                "kotlin/collections/collections.kotlin_builtins",
                "kotlin/annotation/annotation.kotlin_builtins",
                "kotlin/ranges/ranges.kotlin_builtins",
                "kotlin/kotlin.kotlin_builtins",
                "kotlin/coroutines/coroutines.kotlin_builtins" // ✅ новое исключение
            )
        }
    }






    dependencies {
    val bom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(bom)
    androidTestImplementation(bom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation ("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.9.0")
    implementation ("org.jetbrains.kotlin:kotlin-scripting-common:1.9.0")
    implementation ("org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.0")

}
}