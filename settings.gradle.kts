pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Add this new block for toolchain management
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "ORC Scanner"
include(":app")
