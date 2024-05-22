plugins {
    `kotlin-dsl`
    id("org.danilopianini.multi-jvm-test-plugin") version "0.5.8"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

with(extensions.getByType<VersionCatalogsExtension>().named("libs")) {
    dependencies {
        implementation(findLibrary("kotlin-gradle-plugin").get())
        implementation(findLibrary("kotlinpoet").get())
        implementation(findLibrary("arrow").get())
    }
}

sourceSets {
    main {
        kotlin.srcDir("../dsl/src/commonMain")
    }
}