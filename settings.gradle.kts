pluginManagement {
    repositories {
        google()                // <- ESSENCIAL
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()                // <- ESSENCIAL TAMBÉM
        mavenCentral()
    }
}

rootProject.name = "hack-teckmods-app"
include(":app")
