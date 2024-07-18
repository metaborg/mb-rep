plugins {
    `java-library`
    `maven-publish`
    id("org.metaborg.convention.java")
    id("org.metaborg.convention.maven-publish")
}

dependencies {
    api(platform(libs.metaborg.platform)) { version { require("latest.integration") } }

    api(project(":org.spoofax.terms"))
    implementation(libs.jsglr.shared)
    api(libs.interpreter.core)
    implementation(libs.metaborg.util)

    implementation(libs.jakarta.annotation)

    testImplementation(libs.junit)
    testCompileOnly(libs.junit4)
    testImplementation(libs.junit4.benchmarks)
    testCompileOnly(libs.jakarta.annotation)
    testRuntimeOnly(libs.junit.vintage)
}

tasks.test {
    exclude("**/performance/**") // Ignore performance benchmarks.
}
