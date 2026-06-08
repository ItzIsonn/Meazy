plugins {
    java
    kotlin("jvm") version "2.4.0"
}

group = "me.itzisonn_.meazy"
version = "3.0"
description = "Meazy"

val lombokVersion = "1.18.46"
val junitVersion = "6.1.0"



repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.14.0")
    implementation(files("libs/Registry.jar"))

    implementation("org.jspecify:jspecify:1.0.0")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testImplementation("org.junit.platform:junit-platform-suite-api:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}



abstract class DownloadLibsTask : DefaultTask() {
    @TaskAction
    fun action() {
        val path = "libs/Registry.jar"
        val sourceUrl = "https://github.com/ItzIsonn/RegistryLib/releases/download/v1.1/Registry-v1.1.jar"
        download(sourceUrl, path)
    }

    private fun download(url: String, path: String) {
        val destinationFile = File(path)
        if (!destinationFile.exists()) {
            destinationFile.parentFile.mkdirs()
            destinationFile.createNewFile()
        }
        ant.invokeMethod("get", mapOf("src" to url, "dest" to destinationFile))
    }
}

tasks.register<DownloadLibsTask>("downloadLibs") {
    group = "meazy"
    description = "Loads all needed libs"
}