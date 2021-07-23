import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "cn.youyi"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.1"
val junitJupiterVersion = "5.7.0"

val mainClassName = "cn.youyi.dockerv.AppBootstrap"

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
//  implementation("io.vertx:vertx-auth-jwt")
  implementation("io.vertx:vertx-web")
//  implementation("io.vertx:vertx-pg-client")
//  implementation("io.vertx:vertx-mail-client")
//  implementation("io.vertx:vertx-web-templ-freemarker")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.slf4j:slf4j-log4j12:1.7.21")
  implementation("org.slf4j:slf4j-api:1.7.21")
  implementation("org.slf4j:slf4j-api:RELEASE")
  implementation("commons-io:commons-io:2.10.0")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

//val taskBuildWebPages by tasks.register<Exec>("BuildWebPages") {
//  workingDir("./web")
//  commandLine("quasar", "build")
//}

val taskCopyWebPages by tasks.register<Copy>("CopyWebPages") {
  description = "copy the web build files to the /resources"
  from("./web/dist/spa")
  into("./src/main/resources/web")
}

tasks.withType<ShadowJar> {
  dependsOn(taskCopyWebPages)
  archiveClassifier.set("")
  manifest {
    attributes(mapOf("Main-Class" to mainClassName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

