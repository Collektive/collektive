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
    }
}

// The following configuration is redundant since the gradle plugin enables the compiler plugin by default
// It is used to configure the compiler plugin
collektive {
    enabled = true
}

afterEvaluate {
    listOf("uploadKotlinOSSRHToMavenCentralNexus").forEach {
        tasks.named(it).configure {
            enabled = false
        }
    }
}
