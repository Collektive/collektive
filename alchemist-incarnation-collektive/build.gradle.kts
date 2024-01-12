apply(plugin = libs.plugins.kotlin.jvm.id)

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(kotlin("reflect"))
                implementation(libs.bundles.alchemist)
                implementation(rootProject.libs.alchemist.api)
                implementation(rootProject.libs.alchemist)
            }
        }
        val test by getting {
            dependencies {
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
            }
        }
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
