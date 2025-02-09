plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    `maven-publish`
}

group = "com.github.sanctus-corvus"
version = "0.1.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    explicitApi()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name = "Gemini Kotlin Client"
                description = "Kotlin client library for the Gemini API"
                url = "https://github.com/sanctus-corvus/LuminaKt"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "sanctus-corvus"
                        name = "Sanctus Corvus"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/sanctus-corvus/LuminaKt.git"
                    developerConnection = "scm:git:ssh://github.com:sanctus-corvus/LuminaKt.git"
                    url = "https://github.com/sanctus-corvus/LuminaKt"
                }
            }
        }
    }
}