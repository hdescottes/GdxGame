plugins {
    java
    `java-library`
}

allprojects {

    version = "2.6.1"

    apply(plugin = "java")
    apply(plugin = "java-library")

    tasks.register<Test>("unitTest") {
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath
        useJUnitPlatform()
        filter {
            excludeTestsMatching("*IntegrationTest")
        }
    }

    tasks.register<Test>("integrationTest") {
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath
        useJUnitPlatform()
        filter {
            includeTestsMatching("*IntegrationTest")
        }
    }

    repositories {
        mavenCentral() //keep an eye on this, see libgdx 1.10.0
    }

    dependencies {
        testImplementation(rootProject.libs.bundles.junit)
        testImplementation(rootProject.libs.mockito)
        testImplementation(rootProject.libs.assertj)
        testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
    }
}

project(":desktop") {

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(project(":core"))
        implementation(rootProject.libs.gdx.lwjgl3)
        implementation(variantOf(rootProject.libs.gdx.freetype.platform) { classifier("natives-desktop") })
    }
}

project(":core") {

    tasks.named<Copy>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.named<Copy>("processTestResources") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(rootProject.libs.gdx)
        implementation(variantOf(rootProject.libs.gdx.platform) { classifier("natives-desktop") })
        implementation(rootProject.libs.bundles.gdx.vfx)
        implementation(rootProject.libs.logback)
        testImplementation(rootProject.libs.gdx.backend.headless)
    }
}
