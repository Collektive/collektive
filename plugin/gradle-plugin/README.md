# Collektive Gradle Plugin

This plugin is used to apply the Kotlin compiler plugin used for the alignment of aggregate operator.

## Usage

To use this plugin, add the following to your `build.gradle.kts` file:

```kotlin
plugins {
    id("it.unibo.collektive.collektive-plugin") version "<latest version>"
}
```

Simply applying the plugin will apply the Kotlin compiler plugin to the project.  
However, if you want to disable the plugin, you can do so by setting the `collektive.enabled` property to `false`:

```kotlin
collektive {
    enabled = false
}
```

In this case, the auto-alignment of aggregate operators will not be performed.
