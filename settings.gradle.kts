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

// Frontend (web-first Compose Multiplatform). Backend adds ":server" and ":shared" here.
include(":composeApp")
