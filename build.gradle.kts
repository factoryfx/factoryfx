import java.nio.charset.StandardCharsets
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `maven-publish`
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.versions)
    alias(libs.plugins.nmcp)
    alias(libs.plugins.nmcp.aggregation)
    alias(libs.plugins.javafxplugin) apply false
}

// the type-safe `libs` accessor is not visible inside subprojects {} / project(":x") {} blocks,
// so capture the catalog once at script level (see gradle/gradle#22468)
val deps = the<LibrariesForLibs>()

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "9.6.1"
    distributionType = Wrapper.DistributionType.ALL
}

nmcpAggregation {
    centralPortal {
        username.set(System.getenv("SONATYPE_USER"))
        password.set(System.getenv("SONATYPE_PASSWORD"))
        publishingType.set("AUTOMATIC")
    }
}

tasks.named<Zip>("nmcpZipAggregation") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    nmcpAggregation(project(":domFactoryEditing"))
    nmcpAggregation(project(":factory"))
    nmcpAggregation(project(":initializr"))
    nmcpAggregation(project(":javafxDistributionClient"))
    nmcpAggregation(project(":javafxDistributionServer"))
    nmcpAggregation(project(":javafxFactoryEditing"))
    nmcpAggregation(project(":jettyFactory"))
    nmcpAggregation(project(":microserviceRestClient"))
    nmcpAggregation(project(":microserviceRestCommon"))
    nmcpAggregation(project(":microserviceRestResource"))
    nmcpAggregation(project(":oracledbStorage"))
    nmcpAggregation(project(":postgresqlStorage"))
    nmcpAggregation(project(":soapFactory"))
    nmcpAggregation(project(":typescriptGenerator"))
}

subprojects {
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.nmcp")
    apply(plugin = "signing")

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(17)
    }

    if (project.name != "benchmark") { // jmh does not work with module-info
        tasks.named<JavaCompile>("compileJava") {
            inputs.property("moduleName", "io.github.factoryfx.${project.name}")
            val emptyClasspath = project.files()
            doFirst {
                options.compilerArgs = listOf("--module-path", classpath.asPath)
                classpath = emptyClasspath
            }
        }
    }

    tasks.named<Javadoc>("javadoc") {
        isFailOnError = false
        val javadocTask = this
        doFirst {
            (javadocTask.options as StandardJavadocDocletOptions).apply {
                modulePath = javadocTask.classpath.files.toMutableList()
                encoding = StandardCharsets.UTF_8.name()
                addStringOption("Xdoclint:none", "-quiet")
            }
        }
    }

    group = "io.github.factoryfx"
    version = "5.0.0"

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    name.set("factoryfx")
                    packaging = "jar"
                    description.set("factoryfx dependency injection framework")
                    url.set("https://factoryfx.github.io/factoryfx")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("SCOOP Software")
                            name.set("SCOOP Software")
                        }
                    }
                    scm {
                        connection.set("scm:git@github.com:factoryfx/factoryfx.git")
                        developerConnection.set("scm:git@github.com:factoryfx/factoryfx.git")
                        url.set("scm:git@github.com:factoryfx/factoryfx.git")
                    }
                }
            }
        }
    }

    if (System.getenv("SECRING") != null) {
        extensions.configure<SigningExtension> {
            val signingKey = System.getenv("SECRING")
            val signingPassword = System.getenv("SECRING_PASS")
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(extensions.getByType<PublishingExtension>().publications["mavenJava"])
        }
    }

    dependencies {
        "api"(deps.slf4j.api)
        "api"(deps.guava)

        "testImplementation"(deps.mockito.core)
        "testImplementation"(deps.junit.jupiter)
        "testRuntimeOnly"(deps.junit.platform.launcher)
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        failOnNoDiscoveredTests = false
    }

    extensions.configure<com.github.spotbugs.snom.SpotBugsExtension> {
        toolVersion.set(deps.versions.spotbugs.tool)
        ignoreFailures.set(true)
        excludeFilter.set(rootProject.file("findbugs/findbugs-exclude-filter.xml"))
    }

    tasks.named("spotbugsTest") {
        enabled = false
    }
}

project(":microserviceRestResource") {
    dependencies {
        "api"(project(":jettyFactory"))
        "api"(project(":microserviceRestCommon"))
        "testImplementation"(project(":testfactories"))
    }
}

project(":microserviceRestClient") {
    dependencies {
        "api"(project(":factory"))
        "api"(project(":microserviceRestCommon"))
        "api"(deps.jersey.proxy.client)
        "api"(deps.jersey.client)
        "api"(deps.jersey.media.json.jackson)
        "api"(deps.jersey.common)
        "api"(deps.jersey.container.servlet)
    }
}

project(":microserviceRestCommon") {
    dependencies {
        "api"(project(":factory"))
        "api"(deps.jakarta.ws.rs.api)
        "api"(deps.jakarta.activation.api)
        "api"(deps.jakarta.xml.bind.api)
    }
}

project(":microserviceRestIntegrationTest") {
    dependencies {
        "implementation"(project(":microserviceRestResource"))
        "implementation"(project(":microserviceRestClient"))
        "implementation"(deps.logback.classic)
        "implementation"(deps.jakarta.activation.api)
    }
}

project(":factory") {
    dependencies {
        "api"(deps.jackson.databind)
        "api"(deps.jackson.datatype.jdk8)
        "api"(deps.jackson.datatype.jsr310)
        "api"(deps.jackson.jakarta.rs.json.provider)
        "testImplementation"(project(":testfactories"))
        "testImplementation"(deps.logback.classic)
        "testImplementation"(deps.jackson.dataformat.yaml)
    }
}

project(":postgresqlStorage") {
    dependencies {
        "api"(project(":factory"))
        "api"(deps.postgresql)
        "testImplementation"(deps.embedded.postgres)
        "testImplementation"(project(":testfactories"))
    }
}

project(":oracledbStorage") {
    dependencies {
        "implementation"(project(":factory"))
        "implementation"(deps.ojdbc8)
        "testImplementation"(deps.h2)
        "testImplementation"(project(":testfactories"))
    }
}

project(":jettyFactory") {
    dependencies {
        "api"(project(":factory"))
        "api"(deps.jetty.server)
        "api"(deps.jetty.alpn.java.server)
        "api"(deps.jetty.ee10.servlet)
        "api"(deps.jetty.ee10.webapp)
        "api"(deps.jetty.http2.server)
        "api"(deps.jetty.compression.server)
        "api"(deps.jetty.compression.gzip)

        "api"(deps.jakarta.xml.bind.api)

        "api"(deps.jersey.common)
        "api"(deps.jersey.server)
        "api"(deps.jackson.jakarta.rs.json.provider)
        "api"(deps.jersey.container.servlet)
        "api"(deps.jersey.hk2)

        "api"(deps.jakarta.annotation.api)
        "api"(deps.jersey.media.json.jackson)

        "api"(deps.ini4j)

        "testImplementation"(deps.logback.classic)
    }
}

project(":example") {
    apply(plugin = "org.openjfx.javafxplugin")
    extensions.configure<org.openjfx.gradle.JavaFXOptions> {
        version = deps.versions.javafx.get()
        modules = listOf("javafx.controls", "javafx.web", "javafx.graphics", "javafx.fxml", "javafx.media")
        configuration = "api"
    }

    dependencies {
        "implementation"(project(":javafxFactoryEditing"))
        "implementation"(project(":jettyFactory"))
        "implementation"(project(":microserviceRestResource"))
        "implementation"(project(":factory"))
        "implementation"(project(":domFactoryEditing"))

        "implementation"(deps.logback.classic)
        "implementation"(deps.jakarta.activation.api)
    }
}

project(":javafxFactoryEditing") {
    apply(plugin = "org.openjfx.javafxplugin")
    extensions.configure<org.openjfx.gradle.JavaFXOptions> {
        version = deps.versions.javafx.get()
        modules = listOf("javafx.controls", "javafx.web", "javafx.graphics", "javafx.fxml", "javafx.media")
        configuration = "api"
    }

    dependencies {
        "api"(deps.controlsfx)
        "api"(deps.richtextfx)

        "api"(project(":factory"))
        "api"(project(":microserviceRestClient"))

        "testImplementation"(project(":testfactories"))
        "testImplementation"(deps.logback.classic)
        "testImplementation"(deps.jakarta.activation.api)
    }
}

project(":javafxDistributionServer") {
    dependencies {
        "api"(project(":jettyFactory"))
        "api"(project(":factory"))
        "api"(deps.slf4j.api)

        "testImplementation"(deps.logback.classic)
        "testImplementation"(deps.jakarta.activation.api)
    }
}

project(":javafxDistributionClient") {
    apply(plugin = "org.openjfx.javafxplugin")
    extensions.configure<org.openjfx.gradle.JavaFXOptions> {
        version = deps.versions.javafx.get()
        modules = listOf("javafx.controls", "javafx.web", "javafx.graphics", "javafx.fxml", "javafx.media")
        configuration = "api"
    }

    dependencies {
        "api"(deps.slf4j.api)
        "api"(deps.jackson.databind) {
            exclude(group = "javax.annotation")
        }

        "api"(deps.jersey.common)
        "api"(deps.jersey.client)
        "api"(deps.jackson.jakarta.rs.json.provider)
        "api"(deps.jersey.media.json.jackson)
        "api"(deps.jersey.hk2)
        "api"(deps.jakarta.xml.bind.api)
    }
}

project(":testfactories") {
    dependencies {
        "implementation"(project(":factory"))
    }
}

project(":docu") {
    dependencies {
        "implementation"(project(":factory"))
        "implementation"(project(":jettyFactory"))
        "implementation"(project(":postgresqlStorage"))

        "implementation"(project(":microserviceRestResource"))
        "implementation"(project(":microserviceRestClient"))
        "implementation"(project(":initializr"))
        "implementation"(project(":domFactoryEditing"))

        "implementation"(deps.metrics.jetty12)
        "implementation"(deps.embedded.postgres)
        "implementation"(deps.logback.classic)
        "implementation"(deps.jakarta.activation.api)

        "implementation"(deps.jakarta.xml.bind.api)
    }
}

project(":typescriptGenerator") {
    dependencies {
        "api"(project(":factory"))
        "testImplementation"(project(":testfactories"))
        "testImplementation"(project(":jettyFactory"))
        "testImplementation"(project(":domFactoryEditing"))
        "testImplementation"(deps.logback.classic)
    }

    tasks.register<JavaExec>("generateTestCode") {
        classpath = project.extensions.getByType<SourceSetContainer>()["test"].runtimeClasspath
        mainClass.set("io.github.factoryfx.factory.typescript.generator.data.TestGenerator")
    }

    tasks.register<Exec>("installNpm") {
        dependsOn(":typescriptGenerator:generateTestCode")
        workingDir("$projectDir/src/test/ts/")

        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            commandLine("npm.cmd", "install")
        } else {
            commandLine("npm", "install")
        }
    }

    tasks.register<Exec>("typescriptTest") {
        dependsOn(":typescriptGenerator:installNpm")
        workingDir("$projectDir/src/test/ts/")

        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            commandLine("npm.cmd", "test")
        } else {
            commandLine("npm", "test")
        }
    }
}

project(":soapFactory") {
    dependencies {
        "api"(project(":factory"))
        "api"(project(":jettyFactory"))

        "api"(deps.jetty.server) {
            exclude(group = "javax.annotation")
        }
        "api"(deps.jakarta.xml.bind.api)
        "api"(deps.istack.commons.runtime) {
            exclude(group = "javax.annotation")
        }

        "api"(deps.jaxb.runtime)

        "api"(deps.saaj.impl)

        "api"(deps.jakarta.xml.ws.api)
        "api"(deps.jakarta.annotation.api)
        "api"(deps.jakarta.xml.soap.api)
        "api"(deps.jakarta.servlet.api)
    }
}

project(":initializr") {
    dependencies {
        "api"(project(":factory"))
        "api"(project(":jettyFactory"))
        "api"(deps.javapoet)
    }
}

project(":benchmark") {
    dependencies {
        "implementation"(project(":factory"))
        "implementation"(project(":testfactories"))

        "implementation"(deps.jmh.generator.annprocess)
        "implementation"(deps.jmh.core)
    }
}

project(":domFactoryEditing") {
    dependencies {
        "api"(project(":microserviceRestResource"))
        "api"(project(":typescriptGenerator"))
        "testImplementation"(deps.logback.classic)
        "testImplementation"(deps.jetty.compression.server)
        "testImplementation"(deps.jetty.compression.gzip)
    }
}
