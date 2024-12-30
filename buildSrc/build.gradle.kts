plugins {
    `kotlin-dsl`
    id("org.danilopianini.multi-jvm-test-plugin") version "3.1.2"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

with(extensions.getByType<VersionCatalogsExtension>().named("libs")) {
    dependencies {
        implementation(kotlin("reflect"))
        implementation(findLibrary("kotlin-gradle-plugin").get())
        implementation(findLibrary("kotlinpoet").get())
    }
}

sourceSets {
    main {
        kotlin.srcDir("../dsl/src/commonMain")
    }
}