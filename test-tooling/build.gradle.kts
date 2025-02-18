apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(rootProject.libs.bundles.kotlin.testing.common)
                implementation(project(":dsl"))
            }
        }
    }
}
