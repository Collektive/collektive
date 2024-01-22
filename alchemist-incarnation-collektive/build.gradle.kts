import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = libs.plugins.kotlin.jvm.id)

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(kotlin("reflect"))
                implementation(libs.bundles.alchemist)
            }
        }
        val test by getting {
            dependencies {
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
