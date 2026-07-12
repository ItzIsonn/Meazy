plugins {
    kotlin("jvm") version "2.4.0"
}

group = "me.itzisonn_.meazy"
version = "3.0"
description = "Meazy"



repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
}



tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}