/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id(Plugins.ANDROID_LIBRARY)
    kotlin(Plugins.Kotlin.ANDROID)
    kotlin(Plugins.Kotlin.KAPT)
    kotlin(Plugins.Kotlin.ANDROID_EXTENSIONS)
}

android {
    compileSdkVersion(App.Sdk.COMPILE)
    buildToolsVersion(App.Sdk.BUILD_TOOLS_VERSION)

    defaultConfig {
        minSdkVersion(App.Sdk.MIN)
        targetSdkVersion(App.Sdk.TARGET)
        versionCode = App.VERSION_CODE
        versionName = App.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
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

        getByName(App.BuildType.DEBUG) {
        }

        getByName(App.BuildType.RELEASE) {
            isMinifyEnabled = true // To enable proguard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName(App.BuildType.DEBUG).java.srcDir("src/${App.BuildType.DEBUG}/java")
        getByName(App.BuildType.RELEASE).java.srcDir("src/${App.BuildType.RELEASE}/java")
        getByName(App.BuildType.STAGING).java.srcDir("src/${App.BuildType.STAGING}/java")
    }

    testBuildType = App.BuildType.STAGING

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(Module.MODEL))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(project(Module.TEST_SHARED))
    testImplementation(project(Module.ANDROID_TEST_SHARED))

    // Architecture Components
    implementation(Library.LIFECYCLE_EXTENSIONS)
    kapt(Library.LIFECYCLE_COMMON_JAVA_8)
    implementation(Library.ROOM_RUNTIME)
    implementation(Library.ROOM_KTX)
    kapt(Library.ROOM_COMPILER)
    api(Library.WORK_RUNTIME) {
        // WorkManager uses its own version of listenablefuture extracted from guava.
        // This is required to avoid conflicts.
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    // JetPack
    implementation(Library.CORE_KTX)

    // ThreeTenBP for the shared module only. Date and time API for Java.
    testImplementation(Library.THREETENBP)
    compileOnly(Library.THREETENBP_NO_TZDB)

    // Coroutines
    api(Library.COROUTINES_CORE)
    api(Library.COROUTINES_ANDROID)

    // Dagger
    implementation(Library.DAGGER_ANDROID)
    implementation(Library.DAGGER_ANDROID_SUPPORT)
    kapt(Library.DAGGER_COMPILER)
    kapt(Library.DAGGER_ANDROID_PROCESSOR)

    // Timber
    api(Library.TIMBER)

    // Firebase
    api(Library.FIREBASE_CORE)
    api(Library.FIREBASE_AUTH)
    api(Library.FIREBASE_COMMON_KTX)
    api(Library.FIREBASE_ANALYTICS_KTX)
    api(Library.FIREBASE_CRASHLYTICS_KTX)
    api(Library.FIREBASE_PERFORMANCE)
    api(Library.FIREBASE_FIRESTORE)
    api(Library.FIREBASE_FIRESTORE_KTX)
    api(Library.FIREBASE_MESSAGING)

    // Local Unit tests
    testImplementation(Library.JUNIT)
    testImplementation(Library.COROUTINES_TEST)
    testImplementation(Library.MOCKITO_CORE)
    testImplementation(Library.MOCKITO_KOTLIN)
    testImplementation(Library.HAMCREST)
    testImplementation(Library.ARCH_CORE_TESTING)
}
