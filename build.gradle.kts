plugins {
    kotlin("jvm") version "2.4.10"
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
}



tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}