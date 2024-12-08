plugins {
    kotlin("jvm") version "2.1.0"
}

kotlin {
    jvmToolchain(17)
}

group = "cz.libors.aoc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
