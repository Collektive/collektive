import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val Provider<PluginDependency>.id: String get() = get().pluginId

val os: OperatingSystem = OperatingSystem.current()

inline fun <reified ProjectType : KotlinProjectExtension> Project.kotlin(configuration: ProjectType.() -> Unit) =
    extensions.getByType<ProjectType>().configuration()

fun Project.kotlinJvm(configuration: KotlinJvmProjectExtension.() -> Unit) = kotlin(configuration)

fun Project.kotlinMultiplatform(configuration: KotlinMultiplatformExtension.() -> Unit) = kotlin(configuration)

fun Project.configureKotlinMultiplatform() {
    with(extensions.getByType<KotlinMultiplatformExtension>()) {
        val kotlin = this
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            testRuns.getByName("test").executionTask.configure {
                useJUnitPlatform()
                filter {
                    isFailOnNoMatchingTests = false
                }
                testLogging {
                    showExceptions = true
                    events = setOf(
                        TestLogEvent.FAILED,
                        TestLogEvent.PASSED,
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }
        }
        sourceSets.invoke {
            val commonMain by getting
            val commonTest by getting
            val jvmTest by getting
            val nativeMain by creating
            val nativeTest by creating
        }
        js(IR) {
            browser()
            nodejs()
            binaries.library()
        }
        val nativeSetup: KotlinNativeTarget.() -> Unit = {
            compilations["main"].defaultSourceSet.dependsOn(kotlin.sourceSets.getByName("nativeMain"))
            compilations["test"].defaultSourceSet.dependsOn(kotlin.sourceSets.getByName("nativeTest"))
            binaries {
                sharedLib()
                staticLib()
            }
        }
        applyDefaultHierarchyTemplate()
        linuxX64(nativeSetup)
        linuxArm64(nativeSetup)

        mingwX64(nativeSetup)

        macosX64(nativeSetup)
        macosArm64(nativeSetup)
        iosArm64(nativeSetup)
        iosX64(nativeSetup)
        iosSimulatorArm64(nativeSetup)
        watchosArm64(nativeSetup)
        watchosX64(nativeSetup)
        watchosSimulatorArm64(nativeSetup)
        tvosArm64(nativeSetup)
        tvosX64(nativeSetup)
        tvosSimulatorArm64(nativeSetup)
        targets.all {
            compilations.all {
                // enable all warnings as errors
                kotlinOptions {
                    allWarningsAsErrors = true
                }
            }
        }
        // Disable cross compilation
        val excludeTargets = when {
            os.isLinux -> kotlin.targets.filterNot { "linux" in it.name }
            os.isWindows -> kotlin.targets.filterNot { "mingw" in it.name }
            os.isMacOsX -> kotlin.targets.filter { "linux" in it.name || "mingw" in it.name }
            else -> emptyList()
        }.mapNotNull { it as? KotlinNativeTarget }
        configure(excludeTargets) {
            compilations.configureEach {
                cinterops.configureEach { tasks[interopProcessingTaskName].enabled = false }
                compileTaskProvider.get().enabled = false
                tasks[processResourcesTaskName].enabled = false
            }
            binaries.configureEach { linkTask.enabled = false }

            mavenPublication {
                tasks.withType<AbstractPublishToMaven>()
                    .configureEach { onlyIf { publication != this@mavenPublication } }
                tasks.withType<GenerateModuleMetadata>()
                    .configureEach { onlyIf { publication.get() != this@mavenPublication } }
            }
        }
    }
}
