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
include(":quiz_edit")
include(":quiz_solve")
include(":quiz_list")
include(":ai_interactions")
include(":authentication")
include(":common")
include(":data_network")
include(":data_local")
include(":phone_unlock")
