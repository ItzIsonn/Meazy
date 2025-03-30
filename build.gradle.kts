plugins {
    id("java")
    id("io.freefair.lombok") version "8.10.2"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("com.google.code.gson:gson:2.12.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    compileOnly("org.projectlombok:lombok:1.18.36")
}

group = "me.itzisonn_.meazy"
version = "2.6"
description = "Meazy"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
