apply(plugin = libs.plugins.kotlin.jvm.id)

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(rootProject.libs.alchemist.api)
                implementation(rootProject.libs.alchemist)
                implementation(rootProject.libs.bundles.alchemist)
                implementation(project(":alchemist-incarnation-collektive"))
                implementation(project(":dsl"))
            }
        }
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
