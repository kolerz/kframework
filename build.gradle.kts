plugins {
    kotlin("jvm") version "2.4.0-RC"
    id("com.gradleup.shadow") version "9.4.1"
    id("maven-publish")
}

group = "it.kolerz"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "it.kolerz"
                artifactId = "kotlinframework"
                version = "1.0.0"
                artifact(tasks.getByName("shadowJar")) {
                    classifier = ""
                }
            }
        }
    }
}