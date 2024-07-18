plugins {
    `java-library`
    `maven-publish`
    id("org.metaborg.convention.java")
    id("org.metaborg.convention.maven-publish")
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
    }
    test {
        java {
            srcDir("test")
        }
    }
}

dependencies {
    api(platform(libs.metaborg.platform)) { version { require("latest.integration") } }

    implementation(libs.jakarta.annotation)
    testImplementation(libs.junit)
    testCompileOnly(libs.jakarta.annotation)
}

// Copy test resources into classes directory, to make them accessible as classloader resources at runtime.
val copyTestResourcesTask = tasks.create<Copy>("copyTestResources") {
    from("$projectDir/test-resources")
    into("$buildDir/classes/java/test")
}
tasks.getByName("processTestResources").dependsOn(copyTestResourcesTask)
