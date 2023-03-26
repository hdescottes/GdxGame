apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/java/")
        resources.srcDir("src/resources")
    }
    named("test") {
        java.srcDir("src/java/")
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}