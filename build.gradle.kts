import org.gradle.kotlin.dsl.bootBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "3.1.1"
  id("io.spring.dependency-management") version "1.1.0"
  kotlin("jvm") version "1.9.0"
  kotlin("plugin.spring") version "1.9.0"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

extra["springCloudVersion"] = "2022.0.4"
extra["testcontainersVersion"] = "1.19.0"

dependencies {
  implementation("org.flywaydb:flyway-core")

  implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
  runtimeOnly("org.postgresql:postgresql")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.retry:spring-retry")
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.boot:spring-boot-starter-validation")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux")
  testImplementation("org.testcontainers:postgresql")

  // reactive
  //implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  //testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.bootBuildImage {
  imageName.set(project.name)
  environment.set(mapOf("BP_JVM_VERSION" to "17.*"))

  docker {
    publishRegistry {
      project.findProperty("registryUsername")?.let { username.set(it as String) }
      project.findProperty("registryToken")?.let { password.set(it as String) }
      project.findProperty("registryUrl")?.let { url.set(it as String) }
    }
  }
}
