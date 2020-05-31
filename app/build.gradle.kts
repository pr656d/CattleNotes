/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id(BuildPlugins.androidApplicationPlugin)
    id(BuildPlugins.kotlinAndroidPlugin)
    id(BuildPlugins.kotlinKaptPlugin)
    id(BuildPlugins.kotlinAndroidExtensionsPlugin)
    id(BuildPlugins.androidOssLicensesPlugin)
    id(BuildPlugins.kotlinNavigationSafeArgsPlugin)
    id(BuildPlugins.firebaseCrashlyticsPlugin)
    id(BuildPlugins.firebasePerformancePlugin)
    id(BuildPlugins.googleServicesPlugin)
}

android {
    compileSdkVersion(AndroidSdk.compile)
    buildToolsVersion(AndroidSdk.buildToolsVersion)

    defaultConfig {
        applicationId = App.applicationId
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = App.versionCode
        versionName = App.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        manifestPlaceholders = mapOf("crashlyticsEnabled" to true)
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = "-debug"
            manifestPlaceholders = mapOf("crashlyticsEnabled" to false)
            ext["firebaseCrashlytics"] = false
            /*
            isMinifyEnabled = true  // To enable proguard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            */
        }

        getByName("release") {
            manifestPlaceholders = mapOf("crashlyticsEnabled" to true)
            ext["firebaseCrashlytics"] = true
            isMinifyEnabled = true  // To enable proguard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("staging") {
            initWith(getByName("debug"))
            versionNameSuffix = "-staging"

            // Specifies a sorted list of fallback build types that the
            // plugin should try to use when a dependency does not include a
            // "staging" build type.
            // Used with :test-shared, which doesn't have a staging variant.
            matchingFallbacks = listOf("debug")
        }
    }

    sourceSets {
        getByName("debug").java.srcDir("src/debug/java")
        getByName("release").java.srcDir("src/release/java")
        getByName("staging").java.srcDir("src/staging/java")
    }

    testBuildType = "staging"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(Module.shared))
    testImplementation(project(Module.test_shared))
    testImplementation(project(Module.androidTest_shared))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // UI
    implementation(Library.appCompat)
    implementation(Library.cardView)
    implementation(Library.constraintLayout)
    implementation(Library.material)
    implementation(Library.recyclerView)
    implementation(Library.flexbox)

    // Architecture Components
    implementation(Library.lifecycleExtensions)
    kapt(Library.lifecycleCommonJava8)
    implementation(Library.roomRuntime)
    implementation(Library.roomKtx)
    kapt(Library.roomCompiler)

    // JetPack
    implementation(Library.androidxCoreKtx)
    implementation(Library.activityKtx)
    implementation(Library.fragmentKtx)
    implementation(Library.lifecycleRuntimeKtx)
    implementation(Library.navigationUiKtx)
    implementation(Library.navigationFragmentKtx)
    implementation(Library.lifecycleViewModelKtx)
    implementation(Library.lifecycleLiveDataKtx)

    // Date and time API for Java.
    implementation(Library.threeTenABp)
    testImplementation(Library.threeTenBp)

    // Kotlin
    implementation(Library.kotlinStdlibJdk7)

    // Firebase
    implementation(Library.firebaseAuthUi)
    implementation(Library.firebaseCrashlytics)

    // Multidex
    implementation(Library.multidex)

    // Dagger
    implementation(Library.daggerAndroid)
    implementation(Library.daggerAndroidSupport)
    kapt(Library.daggerCompiler)
    kapt(Library.daggerAndroidProcessor)

    // Glide
    implementation(Library.glide)
    kapt(Library.glideCompiler)

    // Timber
    implementation(Library.timber)

    // Json Parser
    implementation(Library.gson)

    // Debugging
    debugImplementation(Library.amitshekharDebugDb)

    // Open-source licences
    implementation(Library.playOssLicenses)

    // Leak canary
//    debugImplementation (Library.leakCanary)

    // Local Unit tests
    testImplementation(Library.junit)
    testImplementation(Library.coroutinesTest)
    testImplementation(Library.mockitoKotlin)
    testImplementation(Library.mockitoCore)
    testImplementation(Library.hamcrest)
    kaptTest(Library.daggerCompiler)
    testImplementation(Library.archCoreTesting)

    // UI Testing
    androidTestImplementation(Library.testRunner)
    androidTestImplementation(Library.testExtJunit)
    androidTestImplementation(Library.espressoCore)
    androidTestImplementation(Library.espressoContrib)
    androidTestImplementation(Library.testRules)
    kaptAndroidTest(Library.daggerCompiler)
}