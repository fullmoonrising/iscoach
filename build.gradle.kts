import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") { enabled = true; archiveBaseName.set(rootProject.name) }
tasks.getByName<Jar>("jar") { enabled = false }

plugins {
    id("org.springframework.boot") version "2.6.8"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.telegram"
version = "3.0.0"
description = "folobot"

repositories {
    mavenCentral()
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // db
    implementation("org.postgresql:postgresql")
    runtimeOnly("org.liquibase:liquibase-core:4.17.2")

    // telegram api
    implementation("org.telegram:telegrambots:6.1.0")

    // serialisation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // testing
    implementation("org.junit.jupiter:junit-jupiter:5.9.0")

    // formatter
    implementation("com.ibm.icu:icu4j:72.1")



//    runtimeOnly("org.postgresql:postgresql")
}

allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embedabble")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("stage") {
    dependsOn("build")
}
