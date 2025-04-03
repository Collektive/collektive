apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(rootProject.libs.bundles.kotlin.testing.common)
                implementation(rootProject.libs.kotlinx.serialization.core)
                implementation(rootProject.libs.kotlinx.serialization.json)
                implementation(rootProject.libs.kotlinx.serialization.protobuf)
                implementation(project(":dsl"))
            }
        }
    }
}
