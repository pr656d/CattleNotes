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
    id(BuildPlugins.androidLibraryPlugin)
    id(BuildPlugins.kotlinAndroidPlugin)
    id(BuildPlugins.kotlinKaptPlugin)
    id(BuildPlugins.kotlinAndroidExtensionsPlugin)
}

android {
    compileSdkVersion(AndroidSdk.compile)
    buildToolsVersion(AndroidSdk.buildToolsVersion)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = App.versionCode
        versionName = App.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("debug") {

        }

        getByName("release") {
            isMinifyEnabled = true  // To enable proguard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(Module.model))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(project(Module.test_shared))
    testImplementation(project(Module.androidTest_shared))

    // Architecture Components
    implementation(Library.lifecycleExtensions)
    kapt(Library.lifecycleCommonJava8)
    implementation(Library.roomRuntime)
    implementation(Library.roomKtx)
    kapt(Library.roomCompiler)
    api(Library.workRuntimeKtx) {
        // WorkManager uses its own version of listenablefuture extracted from guava.
        // This is required to avoid conflicts.
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    // JetPack
    implementation(Library.androidxCoreKtx)

    // ThreeTenBP for the shared module only. Date and time API for Java.
    testImplementation(Library.threeTenBp)
    compileOnly(Library.threeTenBpNoTzdb)

    // Kotlin
    implementation(Library.kotlinStdlibJdk7)

    // Coroutines
    api(Library.coroutinesCore)
    api(Library.coroutinesAndroid)

    // Multidex
    implementation(Library.multidex)

    // Dagger
    implementation(Library.daggerAndroid)
    implementation(Library.daggerAndroidSupport)
    kapt(Library.daggerCompiler)
    kapt(Library.daggerAndroidProcessor)

    // Timber
    api(Library.timber)

    // Firebase
    api(Library.firebaseCore)
    api(Library.firebaseAuth)
    api(Library.firebaseCommonKtx)
    api(Library.firebaseAnalytics)
    api(Library.firebasePerformance)
    api(Library.firebaseFirestore)
    api(Library.firebaseFirestoreKtx)
    api(Library.firebaseMessaging)

    // Local Unit tests
    testImplementation(Library.junit)
    testImplementation(Library.coroutinesTest)
    testImplementation(Library.mockitoCore)
    testImplementation(Library.mockitoKotlin)
    testImplementation(Library.hamcrest)
    testImplementation(Library.archCoreTesting)
}
