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
    id(BuildPlugins.javaLibraryPlugin)
    id(BuildPlugins.kotlinPlugin)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(Library.kotlinStdlibJdk7)

    // ThreeTenBP for the shared module only. Date and time API for Java.
    api(Library.threeTenBpNoTzdb)

    // JetPack
    implementation(Library.androidxCoreKtx)

    // Room
    implementation(Library.roomRuntime)
    implementation(Library.roomKtx)

    // Firebase
    implementation(Library.firebaseFirestoreKtx)

    // Json Parser
    implementation(Library.gson)
}