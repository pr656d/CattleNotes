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

object Library {
    // UI
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    const val CARD_VIEW = "androidx.cardview:cardview:${Versions.SUPPORT_LIBRARY}"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.SUPPORT_LIBRARY}"
    const val FLEX_BOX = "com.google.android:flexbox:${Versions.FLEX_BOX}"

    // Jet pack
    const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:${Versions.ACTIVITY_KTX}"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX}"
    const val LIFECYCLE_RUNTIME_KTX = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_RUNTIME_KTX}"
    const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION_UI_KTX}"
    const val NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_UI_KTX}"
    const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel:${Versions.LIFECYCLE}"
    const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.VIEWMODEL_KTX}"
    const val LIVEDATA = "androidx.lifecycle:lifecycle-livedata:${Versions.LIFECYCLE}"
    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIVEDATA_KTX}"
    const val WORK_RUNTIME = "androidx.work:work-runtime-ktx:${Versions.WORK}"
    const val TEST_RUNNER = "androidx.test:runner:${Versions.TEST_RUNNER}"
    const val TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.TEST_EXT_RUNNER}"
    const val TEST_RULES = "androidx.test:rules:${Versions.TEST_RULES}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
    const val ESPRESSO_CONTRIB = "androidx.test.espresso:espresso-contrib:${Versions.ESPRESSO}"

    // Architecture components
    const val LIFECYCLE_EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:${Versions.LIFECYCLE}"
    const val LIFECYCLE_COMMON_JAVA_8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.LIFECYCLE}"
    const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
    const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"
    const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    const val ARCH_CORE_TESTING = "androidx.arch.core:core-testing:${Versions.ARCH_CORE_TESTING}"

    // Dagger 2
    const val DAGGER_ANDROID = "com.google.dagger:dagger-android:${Versions.DAGGER}"
    const val DAGGER_ANDROID_SUPPORT = "com.google.dagger:dagger-android-support:${Versions.DAGGER}"
    const val DAGGER_COMPILER = "com.google.dagger:dagger-compiler:${Versions.DAGGER}"
    const val DAGGER_ANDROID_PROCESSOR = "com.google.dagger:dagger-android-processor:${Versions.DAGGER}"

    // Coroutines
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"

    // Timber
    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"

    // Gson
    const val GSON = "com.google.code.gson:gson:${Versions.GSON}"

    // Date and time API for java
    const val THREETENABP = "com.jakewharton.threetenabp:threetenabp:${Versions.THREETENABP}"
    const val THREETENBP = "org.threeten:threetenbp:${Versions.THREETENBP}"
    const val THREETENBP_NO_TZDB = "org.threeten:threetenbp:${Versions.THREETENBP}:no-tzdb"

    // Firebase
    const val FIREBASE_CORE = "com.google.firebase:firebase-core:${Versions.FIREBASE_CORE}"
    const val FIREBASE_COMMON_KTX = "com.google.firebase:firebase-common-ktx:${Versions.FIREBASE_COMMON_KTX}"
    const val FIREBASE_AUTH = "com.google.firebase:firebase-auth:${Versions.FIREBASE_AUTH}"
    const val FIREBASE_AUTH_UI = "com.firebaseui:firebase-ui-auth:${Versions.FIREBASE_AUTH_UI}"
    const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore:${Versions.FIREBASE_FIRESTORE}"
    const val FIREBASE_FIRESTORE_KTX = "com.google.firebase:firebase-firestore-ktx:${Versions.FIREBASE_FIRESTORE}"
    const val FIREBASE_ANALYTICS_KTX = "com.google.firebase:firebase-analytics-ktx:${Versions.FIREBASE_ANALYTICS_KTX}"
    const val FIREBASE_CRASHLYTICS_KTX = "com.google.firebase:firebase-crashlytics-ktx:${Versions.FIREBASE_CRASHLYTICS_KTX}"
    const val FIREBASE_PERFORMANCE = "com.google.firebase:firebase-perf:${Versions.FIREBASE_PERFORMANCE}"
    const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging:${Versions.FIREBASE_MESSAGING}"

    // Glide
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"

    // Open source licenses
    const val OSS_LICENSES = "com.google.android.gms:play-services-oss-licenses:${Versions.OSS_LICENSES}"

    // Leak canary
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${Versions.LEAK_CANARY}"

    // Junit
    const val JUNIT = "junit:junit:${Versions.JUNIT}"

    // Mockito
    const val MOCKITO_CORE = "org.mockito:mockito-core:${Versions.MOCKITO}"
    const val MOCKITO_KOTLIN = "com.nhaarman:mockito-kotlin:${Versions.MOCKITO_KOTLIN}"

    // Hamcrest
    const val HAMCREST = "org.hamcrest:hamcrest-library:${Versions.HAMCREST}"
}