plugins {
    java
}

group = "me.itzisonn_.meazy"
version = "2.7"
description = "Meazy"
java.sourceCompatibility = JavaVersion.VERSION_25

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation(files("libs/Registry.jar"))

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
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



abstract class DownloadLibsTask : DefaultTask() {
    @TaskAction
    fun action() {
        val path = "libs/Registry.jar"
        val sourceUrl = "https://github.com/ItzIsonn/RegistryLib/releases/download/v1.1/Registry-v1.1.jar"
        download(sourceUrl, path)
    }

    private fun download(url: String, path: String){
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