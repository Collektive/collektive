apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.arrow.core)
            implementation(libs.hash.sha3)
            implementation(libs.kotlinx.serialization)
        }
        commonTest.dependencies {
            implementation(project(":stdlib"))
            implementation(project(":test-tooling"))
            implementation(libs.bundles.kotlin.test)
        }
    }
}

// The following configuration is redundant since the gradle plugin enables the compiler plugin by default
// It is used to configure the compiler plugin
collektive {
    collektiveEnabled = true
}
