apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/java/")
        resources.srcDir("src/main/resources")
    }
    named("test") {
        java.srcDir("src/test/java/")
        resources.srcDir("src/test/resources")
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