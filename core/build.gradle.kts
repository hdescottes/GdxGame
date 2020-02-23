apply(plugin = "java")

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/java/")
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