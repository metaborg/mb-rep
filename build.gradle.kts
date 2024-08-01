// !! THIS FILE WAS GENERATED USING repoman !!
// Modify `repo.yaml` instead and use `repoman` to update this file
// See: https://github.com/metaborg/metaborg-gradle/

import org.metaborg.convention.Person
import org.metaborg.convention.MavenPublishConventionExtension

// Workaround for issue: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("org.metaborg.convention.root-project")
    alias(libs.plugins.gitonium)
    alias(libs.plugins.spoofax.gradle.langspec) apply false
}

rootProjectConvention {
    // Add `publishAll` and `publish` tasks that delegate to the subprojects and included builds.
    registerPublishTasks.set(true)
}
allprojects {
    apply(plugin = "org.metaborg.gitonium")

    // Configure Gitonium before setting the version
    gitonium {
        mainBranch.set("master")
        tagPrefix.set("devenv-release/")
    }
    version = gitonium.version
    group = "org.metaborg.devenv"

    pluginManager.withPlugin("org.metaborg.convention.maven-publish") {
        extensions.configure(MavenPublishConventionExtension::class.java) {
            repoOwner.set("metaborg")
            repoName.set("mb-rep")

            metadata {
                inceptionYear.set("2006")
                developers.set(listOf(
                    Person("Jeff Smits", email = null, id = "Apanatshka"),
                ))
                contributors.set(listOf(
                    Person("Gabriel Konat", email = null, id = "Gohla"),
                    Person("Lennart Kats", email = null, id = "lennartcl"),
                    Person("Adil Akhter", email = null, id = "adilakhter"),
                    Person("Karl Trygve Kalleberg", email = null, id = "karltk"),
                    Person("Hendrik van Antwerpen", email = null, id = "hendrikvanantwerpen"),
                    Person("Jeff Smits", email = null, id = "Apanatshka"),
                    Person("Daniel A. A. Pelsmaeker", email = null, id = "Virtlink"),
                    Person("Sebastian Erdweg", email = null, id = "seba--"),
                    Person("Jeff Smits", email = null, id = "Apanatshka"),
                    Person("Eduardo Souza", email = null, id = "udesou"),
                ))
            }
        }
    }
}
