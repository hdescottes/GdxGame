apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/java/")
    }
    named("test") {
        java.srcDir("src/java/")
    }
}