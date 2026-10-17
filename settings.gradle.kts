rootProject.name = "bridge"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// One Compose Multiplatform app for web, Android and iOS. The backend is Supabase (see supabase/).
include(":composeApp")
