import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

val Provider<PluginDependency>.id: String get() = get().pluginId

inline fun <reified ProjectType : KotlinProjectExtension> Project.kotlin(configuration: ProjectType.() -> Unit) =
    extensions.getByType<ProjectType>().configuration()

fun Project.kotlinJvm(configuration: KotlinJvmProjectExtension.() -> Unit) = kotlin(configuration)

fun Project.kotlinMultiplatform(configuration: KotlinMultiplatformExtension.() -> Unit) = kotlin(configuration)

@OptIn(ExperimentalKotlinGradlePluginApi::class)
fun Project.configureKotlinMultiplatform() {
    with(extensions.getByType<KotlinMultiplatformExtension>()) {
        compilerOptions {
            allWarningsAsErrors = true
        }
        jvm {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_1_8
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
        js(IR) {
            browser()
            nodejs()
            binaries.library()
        }
        val nativeSetup: KotlinNativeTarget.() -> Unit = {
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

        // Workaround for https://github.com/kotest/kotest/pull/4598 (merged but not released)
        tasks.withType<KotlinCompilationTask<*>>()
            .configureEach {
                compilerOptions {
                    allWarningsAsErrors = !name.contains("test", ignoreCase = true)
                }
            }
    }
}
