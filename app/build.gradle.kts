plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.kakusummer.androidutilsturbo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kakusummer.androidutilsturbo"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        dataBinding = true
    }

    android {
        sourceSets {
            getByName("main") {
                res.srcDirs(
                    "src/main/res",    // 默认的资源目录
                    "src/main/res-sw"  // 添加额外的资源目录
                )
            }
        }
    }

}

dependencies {
    //T0
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //T1
    implementation(libs.androidx.lifecycle.extensions)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.activity.activity.ktx3)
    implementation (libs.androidx.room.runtime) // Room 运行时
    kapt (libs.androidx.room.room.compiler3) // Room 注解处理器
    implementation (libs.androidx.room.ktx) // Room Kotlin 扩展

    //T2
    implementation (libs.github.xxpermissions)
}