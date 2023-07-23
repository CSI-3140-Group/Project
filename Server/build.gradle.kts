import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "group"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.playwright:playwright:1.34.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:2.1.3")
    implementation("org.glassfish.tyrus:tyrus-server:2.1.3")
    implementation("org.glassfish.tyrus:tyrus-container-grizzly-server:2.1.3")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "group.project.Main"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}