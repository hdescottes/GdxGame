val gdxVersion by extra {"1.9.10"}
val box2DLightsVersion by extra {"1.4"}
val jUnitRunnerVersion by extra {"5.0.0-ALPHA"}
val mockitoVersion by extra {"3.3.0"}
val assertJVersion by extra {"3.8.0"}

plugins {
    java
    `java-library`
}

allprojects {

    version = "1.0"

    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.mockito:mockito-core:$mockitoVersion")
        testImplementation("org.junit:junit4-runner:$jUnitRunnerVersion")
        testImplementation("org.assertj:assertj-core:$assertJVersion")
    }
}

project(":desktop") {

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(project(":core"))
        implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
        implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    }
}

project(":core") {

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
        implementation("com.badlogicgames.box2dlights:box2dlights:$box2DLightsVersion")
    }
}
