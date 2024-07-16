import it.unibo.collektive.codegen.CollektiveCodegenTask

apply(plugin = libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

collektive {
    collektiveEnabled = true
}

val generateFieldFunctionsForTypes by tasks.registering(CollektiveCodegenTask::class) {
    group = "code generation"
    description = "Generates Collektive field functions for primitive types"
    outputDir = layout.buildDirectory.dir("generated/kotlin/collektive").get().asFile
}

kotlinMultiplatform {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":dsl"))
            }
            kotlin.srcDirs(generateFieldFunctionsForTypes)
        }
        val commonTest by getting {
            dependencies {
                implementation(rootProject.libs.bundles.kotlin.testing.common)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
            }
        }
    }
}

afterEvaluate {
    listOf("uploadKotlinOSSRHToMavenCentralNexus").forEach {
        tasks.named(it).configure {
            enabled = false
        }
    }
}
