apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/java/")
        resources.srcDir("src/resources")
    }
}