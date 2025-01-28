apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
        }
        commonTest.dependencies {
            implementation(project(":stdlib"))
            implementation(project(":test-tooling"))
            implementation(libs.bundles.kotlin.testing.common)
        }

        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5.jvm)
        }
    }
}

// The following configuration is redundant since the gradle plugin enables the compiler plugin by default
// It is used to configure the compiler plugin
collektive {
    collektiveEnabled = true
}
