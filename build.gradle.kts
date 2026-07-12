plugins {
    kotlin("jvm") version "2.4.0"
}

group = "me.itzisonn_.meazy"
version = "3.0"
description = "Meazy"

val junitVersion = "6.1.0"



repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testImplementation("org.junit.platform:junit-platform-suite-api:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}