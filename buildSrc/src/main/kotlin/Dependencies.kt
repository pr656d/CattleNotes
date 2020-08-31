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

const val kotlinVersion = "1.4.0"

object Module {
    const val app = ":app"
    const val androidTest_shared = ":androidTest-shared"
    const val model = ":model"
    const val shared = ":shared"
    const val test_shared = ":test-shared"
}

object AndroidSdk {
    const val buildToolsVersion = "29.0.2"
    const val min = 21
    const val target = 29
    const val compile = 29
}

object BuildPlugins {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePluginVersion}"
    const val androidApplicationPlugin = "com.android.application"
    const val androidLibraryPlugin = "com.android.library"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val kotlinAndroidPlugin = "kotlin-android"
    const val kotlinAndroidExtensionsPlugin = "kotlin-android-extensions"
    const val kotlinKaptPlugin = "kotlin-kapt"
    const val javaLibraryPlugin = "java-library"
    const val kotlinPlugin = "kotlin"
    const val googleServicesPlugin = "com.google.gms.google-services"
    const val googleServices = "com.google.gms:google-services:${Versions.googleServicesVersion}"
    const val androidOssLicensesPlugin = "com.google.android.gms.oss-licenses-plugin"
    const val androidOssLicensesPluginGradle = "com.google.android.gms:oss-licenses-plugin:${Versions.openSourceLicensesPluginVersion}"
    const val kotlinNavigationSafeArgsPlugin = "androidx.navigation.safeargs.kotlin"
    const val kotlinNavigationSafeArgsPluginGradle = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigationControllerVersion}"
    const val firebaseCrashlyticsPlugin = "com.google.firebase.crashlytics"
    const val firebaseCrashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseCrashlyticsGradleVersion}"
    const val firebasePerformancePlugin = "com.google.firebase.firebase-perf"
    const val firebasePerformancePluginGradle = "com.google.firebase:perf-plugin:${Versions.firebasePerformancePluginVersion}"
}

object Versions {
    const val androidGradlePluginVersion = "4.0.0"
    const val appcompatVersion = "1.1.0"
    const val activityVersion = "1.1.0"
    const val archTestingVersion = "2.1.0"
    const val coreKtxVersion = "1.3.0"
    const val constraintVersion = "1.1.3"
    const val coroutinesVersion = "1.3.7"
    const val debugDbVersion = "1.0.6"
    const val daggerVersion = "2.26"
    const val espressoVersion = "3.1.0"
    const val flexboxVersion = "2.0.1"
    const val fragmentVersion = "1.2.4"
    const val firebaseAnalyticsVersion = "17.4.2"
    const val firebaseAuthVersion = "19.3.1"
    const val firebaseCommonKtxVersion = "19.3.0"
    const val firebaseCoreVersion = "17.4.2"
    const val firebaseCrashlyticsVersion = "2.10.1"
    const val firebaseCrashlyticsGradleVersion = "2.0.0-beta02"
    const val firebaseFirestoreVersion = "21.4.3"
    const val firebaseMessagingVersion = "20.2.0"
    const val firebasePerformanceVersion = "19.0.7"
    const val firebasePerformancePluginVersion = "1.3.1"
    const val firebaseUiAuthVersion = "6.2.0"
    const val glideVersion = "4.10.0"
    const val gsonVersion = "2.8.5"
    const val googleServicesVersion = "4.3.3"
    const val hamcrestVersion = "2.1"
    const val junitVersion = "4.12"
    const val kotlinVersion = "1.3.72"
    const val leakCanaryVersion = "2.2"
    const val lifecycleVersion = "2.2.0"
    const val lifecycleRuntimeVersion = "2.2.0-rc03"
    const val liveDataKtxVersion = "2.2.0"
    const val materialVersion = "1.1.0"
    const val multidexVersion = "2.0.1"
    const val mockitoVersion = "3.0.0"
    const val mockitoKotlinVersion = "1.5.0"
    const val navigationControllerVersion = "2.2.2"
    const val openSourceLicencesVersion = "17.0.0"
    const val openSourceLicensesPluginVersion = "0.10.0"
    const val roomVersion = "2.2.5"
    const val rulesVersion = "1.1.1"
    const val supportLibraryVersion = "1.0.0"
    const val timberVersion = "4.7.1"
    const val testRunnerVersion = "1.1.1"
    const val testExtRunnerVersion = "1.1.0"
    const val threeTenABpVersion = "1.0.5"
    const val threeTenBpVersion = "1.3.6"
    const val viewModelKtxVersion = "2.1.0"
    const val workVersion = "2.3.4"
}

object Library {
    // UI
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appcompatVersion}"
    const val cardView = "androidx.cardview:cardview:${Versions.supportLibraryVersion}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintVersion}"
    const val material = "com.google.android.material:material:${Versions.materialVersion}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.supportLibraryVersion}"
    const val flexbox = "com.google.android:flexbox:${Versions.flexboxVersion}"

    // Jet pack
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.coreKtxVersion}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityVersion}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentVersion}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeVersion}"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigationControllerVersion}"
    const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationControllerVersion}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycleVersion}"
    const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModelKtxVersion}"
    const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycleVersion}"
    const val lifecycleLiveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveDataKtxVersion}"
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.workVersion}"
    const val testRunner = "androidx.test:runner:${Versions.testRunnerVersion}"
    const val testExtJunit = "androidx.test.ext:junit:${Versions.testExtRunnerVersion}"
    const val testRules = "androidx.test:rules:${Versions.rulesVersion}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoVersion}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espressoVersion}"

    // Architecture components
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}"
    const val lifecycleCommonJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycleVersion}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val archCoreTesting = "androidx.arch.core:core-testing:${Versions.archTestingVersion}"

    // Dagger 2
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.daggerVersion}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.daggerVersion}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.daggerVersion}"

    // Coroutines
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}"

    // Timber
    const val timber = "com.jakewharton.timber:timber:${Versions.timberVersion}"

    // Gson
    const val gson = "com.google.code.gson:gson:${Versions.gsonVersion}"

    // Date and time API for java
    const val threeTenABp = "com.jakewharton.threetenabp:threetenabp:${Versions.threeTenABpVersion}"
    const val threeTenBp = "org.threeten:threetenbp:${Versions.threeTenBpVersion}"
    const val threeTenBpNoTzdb = "org.threeten:threetenbp:${Versions.threeTenBpVersion}:no-tzdb"

    // Firebase
    const val firebaseCore = "com.google.firebase:firebase-core:${Versions.firebaseCoreVersion}"
    const val firebaseCommonKtx = "com.google.firebase:firebase-common-ktx:${Versions.firebaseCommonKtxVersion}"
    const val firebaseAuth = "com.google.firebase:firebase-auth:${Versions.firebaseAuthVersion}"
    const val firebaseAuthUi = "com.firebaseui:firebase-ui-auth:${Versions.firebaseUiAuthVersion}"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore:${Versions.firebaseFirestoreVersion}"
    const val firebaseFirestoreKtx = "com.google.firebase:firebase-firestore-ktx:${Versions.firebaseFirestoreVersion}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics:${Versions.firebaseAnalyticsVersion}"
    const val firebaseCrashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.firebaseCrashlyticsVersion}"
    const val firebasePerformance = "com.google.firebase:firebase-perf:${Versions.firebasePerformanceVersion}"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging:${Versions.firebaseMessagingVersion}"

    // MultiDex
    const val multidex = "androidx.multidex:multidex:${Versions.multidexVersion}"

    // Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glideVersion}"

    // Debug DB
    const val amitshekharDebugDb = "com.amitshekhar.android:debug-db:${Versions.debugDbVersion}"

    // Open source licenses
    const val playOssLicenses = "com.google.android.gms:play-services-oss-licenses:${Versions.openSourceLicencesVersion}"

    // Leak canary
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanaryVersion}"

    // Junit
    const val junit = "junit:junit:${Versions.junitVersion}"

    // Mockito
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoVersion}"
    const val mockitoKotlin = "com.nhaarman:mockito-kotlin:${Versions.mockitoKotlinVersion}"

    // Hamcrest
    const val hamcrest = "org.hamcrest:hamcrest-library:${Versions.hamcrestVersion}"
}