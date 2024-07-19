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
extra["testKeycloakVersion"] = "3.3.0"
extra["otelVersion"] = "2.6.0"

dependencies {
  implementation("org.flywaydb:flyway-core")

  implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
  runtimeOnly("org.postgresql:postgresql")
  runtimeOnly("io.micrometer:micrometer-registry-prometheus")
  runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:${property("otelVersion")}")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.retry:spring-retry")
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux")
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("com.github.dasniko:testcontainers-keycloak:${property("testKeycloakVersion")}")
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
