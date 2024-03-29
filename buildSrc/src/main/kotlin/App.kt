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

object App {
    const val ID = "com.pr656d.cattlenotes"
    const val VERSION_NAME = "0.3.7"   // X.Y.Z; X = Major, Y = minor, Z = Patch level
    const val VERSION_CODE = 307 // XYYZZ

    object Sdk {
        const val BUILD_TOOLS_VERSION = "29.0.2"
        const val MIN = 21
        const val TARGET = 29
        const val COMPILE = 29
    }

    object BuildType {
        const val DEBUG = "debug"
        const val RELEASE = "release"
        const val STAGING = "staging"
    }
}