plugins {
    id("it.unibo.collektive.collektive-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(rootProject.libs.arrow)
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
