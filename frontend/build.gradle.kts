plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "hashibane"
version = "0.0.1"


repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    testImplementation("io.kotest:kotest-assertions-core:6.1.1")
    testImplementation("io.kotest:kotest-property:6.1.1")
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}