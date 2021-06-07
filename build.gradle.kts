val gdxVersion by extra {"1.10.0"}
val gdxVfxVersion by extra {"0.5.0"}
val jUnitPlatformVersion by extra {"1.7.1"}
val jUnitJupiterVersion by extra {"5.7.1"}
val mockitoVersion by extra {"3.8.0"}
val assertJVersion by extra {"3.19.0"}
val logbackVersion by extra {"1.3.0-alpha5"}

plugins {
    java
    `java-library`
}

allprojects {

    version = "2.0"

    apply(plugin = "java")
    apply(plugin = "java-library")

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    repositories {
        mavenCentral() //keep an eye on this, see libgdx 1.10.0
    }

    dependencies {
        testImplementation("org.junit.platform:junit-platform-launcher:$jUnitPlatformVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitJupiterVersion")
        testImplementation("org.mockito:mockito-core:$mockitoVersion")
        testImplementation("org.assertj:assertj-core:$assertJVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitJupiterVersion")
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
        implementation("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
        implementation("com.crashinvaders.vfx:gdx-vfx-core:$gdxVfxVersion")
        implementation("com.crashinvaders.vfx:gdx-vfx-effects:$gdxVfxVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
        testImplementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
    }
}
