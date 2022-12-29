package io.github.elisatronetti

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

/**
 * Creating gradle extension which is used to define a property that can be
 * used to enable or disable the plugin.
 */
open class GradleExtension(objects: ObjectFactory) {
    private val enabledProperty: Property<Boolean> = objects.property(Boolean::class.java)
        .apply { convention(true) }

    var enabled: Boolean
        get() = enabledProperty.get()
        set(value) = enabledProperty.set(value)
}
