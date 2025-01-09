apply(plugin = libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(project(":stdlib"))
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

// The following configuration is redundant since the gradle plugin enables the compiler plugin by default
// It is used to configure the compiler plugin
collektive {
    collektiveEnabled = true
}
