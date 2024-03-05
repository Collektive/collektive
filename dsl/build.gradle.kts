apply(plugin = libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(rootProject.libs.arrow)
            }
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

// The following configuration is redundant since the gradle plugin enables the compiler plugin by default
// It is used to configure the compiler plugin
collektive {
    collektiveEnabled = true
}

afterEvaluate {
    listOf("uploadKotlinOSSRHToMavenCentralNexus").forEach {
        tasks.named(it).configure {
            enabled = false
        }
    }
}
