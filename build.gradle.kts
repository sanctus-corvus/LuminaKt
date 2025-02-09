plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    `maven-publish`
}
group = "com.github.sanctus-corvus"
version = "0.1"

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
    jvmToolchain(19)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "lumina-kt"
            version = project.version.toString()

            pom {
                name = "Gemini Kotlin Client"
                description = "Kotlin client library for the Gemini API"
                url = "https://github.com/your-username/your-repo"
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
                    url = "https://github.com/sanctus-corvus"
                }
            }

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "localRepo"
            url = uri("$rootDir/repo")
        }

    }
}