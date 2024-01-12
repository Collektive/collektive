apply(plugin = libs.plugins.kotlin.jvm.id)

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(rootProject.libs.alchemist.api)
                implementation(rootProject.libs.alchemist)
                implementation(project(":alchemist-incarnation-collektive"))
            }
        }
    }
}
