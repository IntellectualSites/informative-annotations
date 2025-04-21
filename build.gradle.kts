import java.net.URI
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    java
    `java-library`
    signing

    id("com.diffplug.spotless") version "7.0.2"
    id("com.vanniktech.maven.publish") version "0.31.0"

    idea
    eclipse
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

version = "1.6"
group = "com.intellectualsites.informative-annotations"

repositories {
    mavenCentral()
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("LICENSE"))
        target("**/*.java")
    }
}

tasks {

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.noTimestamp()
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}

signing {
    if (!project.hasProperty("skip.signing") && !version.toString().endsWith("-SNAPSHOT")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        signing.isRequired
        sign(publishing.publications)
    }
}

mavenPublishing {

    coordinates(
        groupId = "$group",
        artifactId = project.name,
        version = "${project.version}",
    )

    pom {
        name.set(project.name)
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
                organizationUrl.set("https://github.com/IntellectualSites/")
                email.set("contact(at)notmyfault.dev")
            }
        }

        scm {
            url.set("https://github.com/IntellectualSites/informative-annotations")
            connection.set("scm:git:https://github.com/IntellectualSites/informative-annotations.git")
            developerConnection.set("scm:git:git@github.com:IntellectualSites/informative-annotations.git")
            tag.set("${project.version}")
        }

        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/IntellectualSites/informative-annotations/issues")
        }

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    }
}
