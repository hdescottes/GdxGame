apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/java/")
    }
    named("test") {
        java.srcDir("src/java/")
    }
}