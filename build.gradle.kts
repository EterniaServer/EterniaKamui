plugins {
    id("java")
    id("jacoco")
    id("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

jacoco {
    toolVersion = "0.8.8"
}

sonarqube  {
    properties {
        property("sonar.projectName", project.name)
        property("sonar.projectKey", "EterniaServer_EterniaKamui")
        property("sonar.organization", "eterniaserver")
        property("sonar.projectVersion", "${project.version}")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.binaries", "build/classes")
        property("sonar.java.libraries", "build/libs")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.verbose", "true")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.junit.reportsPath", "build/test-results/test")
    }
}

group = "br.com.eterniaserver"
version = "4.3.0"

repositories {
    mavenCentral()
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "papi-repo"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        name = "scarsz"
        url = uri("https://nexus.scarsz.me/content/groups/public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "eternialib-repo"
        url = uri("https://maven.pkg.github.com/eterniaserver/eternialib")
        credentials {
            username = System.getenv("USERNAME")
            password = System.getenv("TOKEN")
        }
    }
    maven {
        name = "eterniaserver-repo"
        url = uri("https://maven.pkg.github.com/eterniaserver/eterniaserver")
        credentials {
            username = System.getenv("USERNAME")
            password = System.getenv("TOKEN")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup ("maven.modrinth")
        }
    }
    mavenLocal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    compileOnly("br.com.eterniaserver", "eternialib", "4.3.0")
    compileOnly("br.com.eterniaserver", "eterniaserver", "4.0.10")
    compileOnly("com.github.TechFortress", "GriefPrevention", "16.18.1")
    compileOnly("maven.modrinth", "pl3xmap", "1.20.4-483")
    implementation("org.bstats", "bstats-bukkit", "3.0.0")
    testImplementation("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.9.2")
    testImplementation("org.mockito", "mockito-inline", "5.2.0")
}

tasks.shadowJar {
    listOf("org.bstats").forEach {
        relocate(it, "${rootProject.group}.lib.$it")
    }
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    archiveVersion.set("${project.version}")
}

tasks.test {
    useJUnitPlatform()

    dependsOn("cleanTest")

    testLogging {
        events("passed", "skipped", "failed")
    }

    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

tasks.named("sonarqube").configure {
    dependsOn("test")
}

tasks.named("build").configure {
    dependsOn("shadowJar")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
        filteringCharset = "UTF-8"
    }
}