object Plugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val JAVA_LIBRARY = "java-library"
    const val KOTLIN = "kotlin"
    const val GOOGLE_SERVICES = "com.google.gms.google-services"
    const val OSS_LICENSES = "com.google.android.gms.oss-licenses-plugin"
    const val NAVIGATION_SAFEARGS = "androidx.navigation.safeargs.kotlin"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase.crashlytics"
    const val FIREBASE_PERFORMANCE = "com.google.firebase.firebase-perf"
    const val SPOTLESS = "com.diffplug.spotless"

    /**
     * Plugins which can be applied as
     *      plugins {
     *          kotlin(Plugins.Kotlin.Name)
     *      }
     */
    object Kotlin {
        const val ANDROID = "android"
        const val KAPT = "kapt"
        const val ANDROID_EXTENSIONS = "android.extensions"
    }
}