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
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // ThreeTenBP for the shared module only. Date and time API for Java.
    api(Library.THREETENBP_NO_TZDB)

    // JetPack
    implementation(Library.CORE_KTX)

    // Room
    implementation(Library.ROOM_RUNTIME)
    implementation(Library.ROOM_KTX)

    // Firebase
    implementation(Library.FIREBASE_FIRESTORE_KTX)

    // Json Parser
    implementation(Library.GSON)
}
