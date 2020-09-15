import com.diffplug.gradle.spotless.SpotlessExtension

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

buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(ClassPath.ANDROID_BUILD)
        classpath(ClassPath.KOTLIN)
        classpath(ClassPath.GOOGLE_SERVICES)
        classpath(ClassPath.FIREBASE_CRASHLYTICS)
        classpath(ClassPath.FIREBASE_PERFORMANCE)
        classpath(ClassPath.NAVIGATION_SAFE_ARGS)
        classpath(ClassPath.OSS_LICENSES)
        classpath(ClassPath.SPOTLESS)
    }
}

plugins {
    id(Plugins.SPOTLESS) version Versions.SPOTLESS
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    apply(plugin = Plugins.SPOTLESS)
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint(Versions.KTLINT).userData(
                mapOf(
                    "max_line_length" to "100",
                    "disabled_rules" to "import-ordering",
                    "indent_size" to "4",
                    "continuation_indent_size" to "4"
                )
            )
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
            licenseHeaderFile(project.rootProject.file("copyright.kt"))
        }
        kotlinGradle {
            // same as kotlin, but for .gradle.kts files (defaults to '*.gradle.kts')
            target("**/*.gradle.kts")
            ktlint(Versions.KTLINT)
            licenseHeaderFile(project.rootProject.file("copyright.kt"), "(plugins |import |include)")
        }
    }

    tasks.whenTaskAdded {
        when (name) {
            "preBuild" -> {
                dependsOn("spotlessApply", "spotlessCheck")
            }
        }
    }
}
