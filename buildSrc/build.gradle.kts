plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

with(extensions.getByType<VersionCatalogsExtension>().named("libs")) {
    dependencies {
        implementation(kotlin("reflect"))
        implementation(findLibrary("kotlin-gradle-plugin").get())
    }
}
