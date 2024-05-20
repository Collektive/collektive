import it.unibo.collektive.CollektiveCodegenTask

apply(plugin = libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

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
    }
}
