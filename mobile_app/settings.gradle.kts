pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "ExamHacker Mobile"
include(":app")
include(":resources")
include(":settings")
include(":quiz_create")
include(":quiz_solve")
include(":quiz_list")
include(":authentication")
include(":common")
include(":data_network")
include(":data_local")
include(":phone_unlock")
include(":quiz_hub")
include(":profile")
include(":quiz_info")
include(":quiz_edit")
include(":domain")
