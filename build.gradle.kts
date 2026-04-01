plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "cz.cvut.fel.plichjan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Existing dependencies from Maven
    implementation("log4j:log4j:1.2.13")
    testImplementation("junit:junit:4.10")
    testImplementation("com.google.guava:guava:16.0.1")

    // Kotlin Scripting for JSR-223
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.22")
}

application {
    mainClass.set("cz.cvut.fel.plichjan.ViewerFrameKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
    }
}
