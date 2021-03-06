import java.net.URI

plugins {
    java
    `java-library`
    `maven-publish`
    signing

    id("org.cadixdev.licenser") version "0.6.1"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"

    idea
    eclipse
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

version = "1.3-SNAPSHOT"
group = "com.intellectualsites.informative-annotations"

repositories {
    mavenCentral()
}

configure<org.cadixdev.gradle.licenser.LicenseExtension> {
    header(rootProject.file("LICENSE"))
    include("**/*.java")
    newLine.set(false)
}

tasks {

    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial"))
            options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    if (!version.toString().endsWith("-SNAPSHOT")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        signing.isRequired
        sign(publishing.publications)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set(project.name + " " + project.version)
                description.set("An informative annotation library.")
                url.set("https://github.com/IntellectualSites/informative-annotations")

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("NotMyFault")
                        name.set("Alexander Brandes")
                        organization.set("IntellectualSites")
                        email.set("contact@notmyfault.dev")
                    }
                }

                scm {
                    url.set("https://github.com/IntellectualSites/informative-annotations")
                    connection.set("scm:https://IntellectualSites@github.com/IntellectualSites/informative-annotations.git")
                    developerConnection.set("scm:git://github.com/IntellectualSites/informative-annotations.git")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/IntellectualSites/informative-annotations/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
