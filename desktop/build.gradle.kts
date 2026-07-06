apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/java/")
    }
    named("test") {
        java.srcDir("src/test/java/")
    }
}

tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs the game"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.gdx.game.desktop.DesktopLauncher")
}