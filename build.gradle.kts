import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "2.3.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    `maven-publish`
}

group = "io.github.team-sneakymouse"

version =
    providers.exec {
        workingDir(rootDir)
        commandLine("git", "show", "-s", "--format=%ct:%h", "--abbrev=12", "HEAD")
    }.standardOutput.asText.map { commit ->
        val (timestamp, hash) = commit.trim().split(":", limit = 2)
        val date =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").withZone(ZoneOffset.UTC)
                .format(Instant.ofEpochSecond(timestamp.toLong()))
        "$date-$hash"
    }.get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.sneakyrp.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("io.github.team-sneakymouse:sneakyutils:2026.10.18-4b5831b0b997")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.3.21")
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks {
    processResources {
        inputs.property("version", project.version.toString())
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version.toString())
        }
    }
    runServer {
        minecraftVersion("26.2")
    }
    jar {
        archiveBaseName.set(rootProject.name)
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = rootProject.name
            from(components["java"])
            pom {
                name.set("SneakyNotes")
                description.set("Paper plugin to create temporary floating notes.")
                url.set("https://github.com/Team-Sneakymouse/SneakyNotes")
                properties.put("sneakyrp.deployment.type", "paper-plugin")
                scm {
                    url.set("https://github.com/Team-Sneakymouse/SneakyNotes")
                    connection.set("scm:git:https://github.com/Team-Sneakymouse/SneakyNotes.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "sneakyrp"
            url = uri("https://maven.sneakyrp.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<org.gradle.authentication.http.BasicAuthentication>("basic")
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.check)
}
