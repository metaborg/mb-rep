plugins {
    `java-library`
    id("org.metaborg.convention.java")
    id("org.metaborg.convention.maven-publish")
    id("org.metaborg.convention.junit")
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
    testCompileOnly(libs.jakarta.annotation)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

// Copy test resources into classes directory, to make them accessible as classloader resources at runtime.
val copyTestResourcesTask = tasks.create<Copy>("copyTestResources") {
    from("$projectDir/test-resources")
    into("$buildDir/classes/java/test")
}
tasks.getByName("processTestResources").dependsOn(copyTestResourcesTask)
