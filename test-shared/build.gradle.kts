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
    implementation(project(":model"))

    // ThreeTenBP for the shared module only. Date and time API for Java.
    testImplementation(Library.threeTenBp)
    compileOnly(Library.threeTenBpNoTzdb)

    implementation(Library.junit)
    implementation(Library.coroutinesTest)
}
