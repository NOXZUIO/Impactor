import extensions.getLatestGitCommitHash

plugins {
    `java-library`
    id("org.cadixdev.licenser")
    id("net.kyori.blossom")
    kotlin("jvm")
}

repositories {
    // Vendored fallback for net.kyori:event-api:5.0.0-SNAPSHOT.
    // The upstream KyoriPowered/event project was archived on 2023-07-16 and its
    // snapshot host (s01.oss.sonatype.org) has since been decommissioned, so this
    // coordinate is no longer resolvable from ANY public repository (Central,
    // Sonatype's new Central Portal snapshots, Fabric, Architectury, etc. all
    // 404 on it). This repo serves a copy built from upstream source; see
    // libs/vendor-repo/README.md for how it was produced and how to refresh it.
    maven {
        name = "Vendored-Event-Api"
        url = uri(rootProject.layout.projectDirectory.dir("libs/vendor-repo"))
    }
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://libraries.minecraft.net")
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "Sonatype Central Snapshots"
    }
    maven {
        name = "luck-repo"
        url = uri("https://repo.lucko.me")
        content {
            includeModule("me.lucko", "spark-api")
        }
    }

}

version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        dependsOn(updateLicenses)
        finalizedBy(test)
    }

    jar {
        if(project.parent?.name.equals("api")) {
            archiveBaseName.set("Impactor-API-${project.name.substring(0, 1).toUpperCase()}${project.name.substring(1)}")
        } else {
            archiveBaseName.set("Impactor-${project.name.substring(0, 1).toUpperCase()}${project.name.substring(1)}")
        }
        archiveClassifier.set("dev-slim")
    }
}

license {
    header(rootProject.file("HEADER.txt"))
    properties {
        this.set("name", "Impactor")
        this.set("url", "https://github.com/NickImpact/Impactor/")
        this.set("year", 2022)
    }
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
                property("commit", project.getLatestGitCommitHash())
            }
        }
    }
}
