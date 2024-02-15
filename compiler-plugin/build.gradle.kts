import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.danilopianini.gradle.mavencentral.DocStyle

plugins {
    alias(libs.plugins.build.config)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
}

group = "it.unibo.collektive"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
}

buildConfig {
    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"$group.compiler-plugin\"")
}

tasks.withType<KotlinCompile<*>>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xcontext-receivers")
    }
}

signing {
    if (System.getenv("CI") == "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

publishOnCentral {
    val githubSlug = "Collektive/collektive"
    projectUrl = "https://github.com/$githubSlug"
    projectDescription = "A Kotlin compiler plugin performing transparent aggregate alignment"
    licenseName = "Apache License 2.0"
    projectLongName = "Collektive kotlin compiler plugin"
    licenseUrl = "https://opensource.org/license/Apache-2.0/"
    docStyle = DocStyle.HTML
    publishing {
        publications {
            withType<MavenPublication>().configureEach {
                if ("OSSRH" !in name) {
                    artifact(tasks.javadocJar)
                }
                scmConnection = "git:git@github.com:$githubSlug"
                projectUrl = "https://github.com/$githubSlug"
                pom {
                    developers {
                        developer {
                            name = "Elisa Tronetti"
                            email = "elisa.tronetti@studio.unibo.it"
                            url = "https://github.com/ElisaTronetti"
                        }
                        developer {
                            name = "Nicolas Farabegoli"
                            email = "nicolas.farabegoli@unibo.it"
                            url = "https://nicolasfarabegoli.it"
                        }
                        developer {
                            name = "Danilo Pianini"
                            email = "danilo.pianin@unibo.it"
                            url = "https://danysk.github.io"
                        }
                    }
                }
            }
        }
    }
}
