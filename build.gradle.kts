plugins {
    java
    kotlin("jvm") version "1.9.22"
    alias(libs.plugins.architectury)
    alias(libs.plugins.loom)
    alias(libs.plugins.gradle.shadow)
}

group = "org.example"
version = "1.1"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

val shadowCommon: Configuration by configurations.creating

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "Sonatype Snapshots"
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
        name = "Sonatype 01 Snapshots"
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings("net.fabricmc:yarn:${libs.versions.yarn.get()}:v2")

    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.language.kotlin)

//    Fabric API
    modImplementation(libs.fabric.api)
    modImplementation(fabricApi.module("fabric-command-api-v2", libs.versions.fabric.api.get()))

//    Junit Jupiter
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    modApi(libs.impactor.api.economy)

//    MiniMessage
    implementation(libs.net.kyori.adventure.api)
    shadowCommon(libs.net.kyori.adventure.api)
    implementation(libs.net.kyori.adventure.text.minimessage)
    shadowCommon(libs.net.kyori.adventure.text.minimessage)
    implementation(libs.net.kyori.adventure.text.serializer.gson)
    shadowCommon(libs.net.kyori.adventure.text.serializer.gson)

//    MongoDB Drivers
    implementation(libs.db.mongo.driver.core)
    shadowCommon(libs.db.mongo.driver.core)
    implementation(libs.db.mongo.driver.sync)
    shadowCommon(libs.db.mongo.driver.sync)
    api(libs.db.mongo.bson.kotlin)
    shadowCommon(libs.db.mongo.bson.kotlin)

//    Luckperms
    compileOnly(libs.luckperms)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

kotlin.target.compilations.all {
    kotlinOptions.jvmTarget = "21"
}

tasks.shadowJar {
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier.set("fabric")
}