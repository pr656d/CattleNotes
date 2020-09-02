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
    id(Plugins.ANDROID_APPLICATION)
    kotlin(Plugins.Kotlin.ANDROID)
    kotlin(Plugins.Kotlin.KAPT)
    kotlin(Plugins.Kotlin.ANDROID_EXTENSIONS)
    id(Plugins.OSS_LICENSES)
    id(Plugins.NAVIGATION_SAFEARGS)
    id(Plugins.FIREBASE_CRASHLYTICS)
    id(Plugins.FIREBASE_PERFORMANCE)
}

android {
    compileSdkVersion(App.Sdk.COMPILE)
    buildToolsVersion(App.Sdk.BUILD_TOOLS_VERSION)

    defaultConfig {
        applicationId = App.ID
        minSdkVersion(App.Sdk.MIN)
        targetSdkVersion(App.Sdk.TARGET)
        versionCode = App.VERSION_CODE
        versionName = App.VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders = mapOf("crashlyticsEnabled" to true)
        vectorDrawables.useSupportLibrary = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.incremental"] = "true"
            }
        }
    }

    buildTypes {
        getByName(App.BuildType.DEBUG) {
            versionNameSuffix = "-${App.BuildType.DEBUG}"
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

        getByName(App.BuildType.RELEASE) {
            manifestPlaceholders = mapOf("crashlyticsEnabled" to true)
            ext["firebaseCrashlytics"] = true
            isMinifyEnabled = true  // To enable proguard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        maybeCreate(App.BuildType.STAGING)
        getByName(App.BuildType.STAGING) {
            initWith(getByName(App.BuildType.DEBUG))
            versionNameSuffix = "-${App.BuildType.STAGING}"

            // Specifies a sorted list of fallback build types that the
            // plugin should try to use when a dependency does not include a
            // "staging" build type.
            // Used with :test-shared, which doesn't have a staging variant.
            matchingFallbacks = listOf(App.BuildType.DEBUG)
        }
    }

    sourceSets {
        getByName(App.BuildType.DEBUG).java.srcDir("src/${App.BuildType.DEBUG}/java")
        getByName(App.BuildType.RELEASE).java.srcDir("src/${App.BuildType.RELEASE}/java")
        getByName(App.BuildType.STAGING).java.srcDir("src/${App.BuildType.STAGING}/java")
    }

    testBuildType = App.BuildType.STAGING

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // To avoid the compile error: "Cannot inline bytecode built with JVM target 1.8
    // into bytecode that is being built with JVM target 1.6"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(Module.SHARED))
    testImplementation(project(Module.TEST_SHARED))
    testImplementation(project(Module.ANDROID_TEST_SHARED))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // UI
    implementation(Library.APP_COMPAT)
    implementation(Library.CARD_VIEW)
    implementation(Library.CONSTRAINT_LAYOUT)
    implementation(Library.MATERIAL)
    implementation(Library.RECYCLER_VIEW)
    implementation(Library.FLEX_BOX)

    // Architecture Components
    implementation(Library.LIFECYCLE_EXTENSIONS)
    kapt(Library.LIFECYCLE_COMMON_JAVA_8)
    implementation(Library.ROOM_RUNTIME)
    implementation(Library.ROOM_KTX)
    kapt(Library.ROOM_COMPILER)

    // JetPack
    implementation(Library.CORE_KTX)
    implementation(Library.ACTIVITY_KTX)
    implementation(Library.FRAGMENT_KTX)
    implementation(Library.LIFECYCLE_RUNTIME_KTX)
    implementation(Library.NAVIGATION_UI_KTX)
    implementation(Library.NAVIGATION_FRAGMENT_KTX)
    implementation(Library.VIEWMODEL_KTX)
    implementation(Library.LIVEDATA_KTX)

    // Date and time API for Java.
    implementation(Library.THREETENABP)
    testImplementation(Library.THREETENBP)

    // Firebase
    implementation(Library.FIREBASE_AUTH_UI)

    // Dagger
    implementation(Library.DAGGER_ANDROID)
    implementation(Library.DAGGER_ANDROID_SUPPORT)
    kapt(Library.DAGGER_COMPILER)
    kapt(Library.DAGGER_ANDROID_PROCESSOR)

    // Glide
    implementation(Library.GLIDE)
    kapt(Library.GLIDE_COMPILER)

    // Timber
    implementation(Library.TIMBER)

    // Json Parser
    implementation(Library.GSON)

    // Open-source licences
    implementation(Library.OSS_LICENSES)

    // Leak canary
//    debugImplementation (Library.leakCanary)

    // Local Unit tests
    testImplementation(Library.JUNIT)
    testImplementation(Library.COROUTINES_TEST)
    testImplementation(Library.MOCKITO_KOTLIN)
    testImplementation(Library.MOCKITO_CORE)
    testImplementation(Library.HAMCREST)
    kaptTest(Library.DAGGER_COMPILER)
    testImplementation(Library.ARCH_CORE_TESTING)

    // UI Testing
    androidTestImplementation(Library.TEST_RUNNER)
    androidTestImplementation(Library.TEST_EXT_JUNIT)
    androidTestImplementation(Library.ESPRESSO_CORE)
    androidTestImplementation(Library.ESPRESSO_CONTRIB)
    androidTestImplementation(Library.TEST_RULES)
    kaptAndroidTest(Library.DAGGER_COMPILER)
}

apply(plugin = Plugins.GOOGLE_SERVICES)
