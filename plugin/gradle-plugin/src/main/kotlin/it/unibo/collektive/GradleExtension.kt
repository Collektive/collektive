package it.unibo.collektive

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

/**
 * Creating gradle extension which is used to define a property that can be
 * used to enable or disable the plugin.
 */
open class GradleExtension(objects: ObjectFactory) {
    /**
     * Determines if the compiler plugin should be enabled or disabled.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).apply { convention(true) }
}
