plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
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

val spoofax2Version: String by ext
dependencies {
  api(platform("org.metaborg:parent:$spoofax2Version"))

  compileOnly("jakarta.annotation:jakarta.annotation-api")

  testCompileOnly("jakarta.annotation:jakarta.annotation-api")
}

// Copy test resources into classes directory, to make them accessible as classloader resources at runtime.
val copyTestResourcesTask = tasks.create<Copy>("copyTestResources") {
  from("$projectDir/test-resources")
  into("$buildDir/classes/java/test")
}
tasks.getByName("processTestResources").dependsOn(copyTestResourcesTask)
