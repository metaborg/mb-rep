plugins {
    id("org.metaborg.gradle.config.java-library")
    id("org.metaborg.gradle.config.junit-testing")
}

fun compositeBuild(name: String) = "$group:$name:$version"
val spoofax2Version: String by ext
dependencies {
    api(platform("org.metaborg:parent:$spoofax2Version"))

    api(project(":org.spoofax.terms"))
    implementation(compositeBuild("jsglr.shared"))
    api(compositeBuild("org.spoofax.interpreter.core"))
    implementation(compositeBuild("org.metaborg.util"))

    implementation("jakarta.annotation:jakarta.annotation-api")

    testCompileOnly("junit:junit")
    testImplementation("com.carrotsearch:junit-benchmarks")
    testCompileOnly("jakarta.annotation:jakarta.annotation-api")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.1.0")
}

tasks.test {
    exclude("**/performance/**") // Ignore performance benchmarks.
}
