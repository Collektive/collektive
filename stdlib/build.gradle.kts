import it.unibo.collektive.codegen.CollektiveCodegenTask

apply(plugin = libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

collektive {
    collektiveEnabled = true
}

val generateFieldFunctionsForTypes by tasks.registering(CollektiveCodegenTask::class) {
    group = "code generation"
    description = "Generates Collektive field functions for primitive types"
    outputDir =
        layout.buildDirectory
            .dir("generated/kotlin/collektive")
            .get()
            .asFile
}

// Avoid verification tasks to complain about being not dependent on the code generation tasks
tasks.withType<SourceTask>().configureEach {
    if (this is VerificationTask) {
        dependsOn(generateFieldFunctionsForTypes)
    }
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
                implementation(project(":test-tooling"))
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
