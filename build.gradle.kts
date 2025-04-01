plugins {
    id("java")
    id("io.freefair.lombok") version "8.13.1"
}

group = "me.itzisonn_.meazy"
version = "2.6"
description = "Meazy"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("com.google.code.gson:gson:2.12.1")

    implementation(files("libs/Registry.jar"))

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

abstract class DownloadLibsTask : DefaultTask() {
    @TaskAction
    fun action() {
        val path = "libs/Registry.jar"
        val sourceUrl = "https://github.com/ItzIsonn/RegistryLib/releases/download/v1.0/Registry.jar"
        download(sourceUrl, path)
    }

    private fun download(url : String, path : String){
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