plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
}

fun compositeBuild(name: String) = "$group:$name:$version"
val spoofax2Version: String by ext
dependencies {
  api(platform("org.metaborg:parent:$spoofax2Version"))

  api(project(":org.spoofax.terms"))
  implementation(compositeBuild("org.spoofax.jsglr"))
  api(compositeBuild("org.spoofax.interpreter.core"))
  implementation(compositeBuild("org.metaborg.util"))

  implementation("com.google.guava:guava")

  compileOnly("com.google.code.findbugs:jsr305")

  testCompileOnly("junit:junit")
  testImplementation("com.carrotsearch:junit-benchmarks")
  testCompileOnly("com.google.code.findbugs:jsr305")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.1.0")
}

tasks.test {
  exclude("**/performance/**") // Ignore performance benchmarks.
}
