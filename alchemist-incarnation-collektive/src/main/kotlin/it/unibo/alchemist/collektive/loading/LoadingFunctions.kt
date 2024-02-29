package it.unibo.alchemist.collektive.loading

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Position
import it.unibo.collektive.aggregate.api.Aggregate
import kotlin.reflect.jvm.kotlinFunction

internal fun entrypointStyleSelector(entrypoint: String): EntrypointStyle? {
    val fqnRegex = "^(?:\\w+\\.)+\\w+\$".toRegex()
    val inlineRegex =
        Regex("\\{\\s*name=(.*?),\\s*code=(.*?),\\s*entrypoint=(.*?)\\s*}", RegexOption.DOT_MATCHES_ALL)
    val sourceRegex = Regex(
        "\\{\\s*name=(.*?),\\s*source-sets=\\[(.*?)],\\s*code=(.*?),\\s*entrypoint=(.*?)\\s*}",
        RegexOption.DOT_MATCHES_ALL,
    )

    return when {
        fqnRegex.matches(entrypoint) -> LoadFromEntrypoint(entrypoint)
        inlineRegex.matches(entrypoint) -> inlineRegex.matchEntire(entrypoint)?.destructured?.let { (name, code, ep) ->
            LoadFromInline(name, code, ep)
        }

        sourceRegex.matches(entrypoint) ->
            sourceRegex.matchEntire(entrypoint)?.destructured?.let { (name, sourceSets, code, ep) ->
                LoadFromSource(name, sourceSets.split(","), code, ep)
            }

        else -> null
    }
}

internal fun <P : Position<P>> entrypointFromRunProgram(
    entrypoint: String,
    localDevice: CollektiveDevice<P>,
): context(CollektiveDevice<P>) Aggregate<Int>.() -> Any? {
    val className = entrypoint.substringBeforeLast(".")
    val methodName = entrypoint.substringAfterLast(".")
    val clazz = Class.forName(className)
    val method = clazz.methods.find { it.name == methodName }
        ?: error("Entrypoint $entrypoint not found, no method $methodName found in class $className")
    val ktfunction = checkNotNull(method.kotlinFunction) {
        "Method $methodName in class $className cannot be converted to a Kotlin function"
    }
    var parameters: Array<Any?> = emptyArray()
    return {
        if (parameters.isEmpty()) {
            parameters = method.parameters.map {
                when {
                    it.type.isAssignableFrom(Aggregate::class.java) -> this
                    it.type.isAssignableFrom(CollektiveDevice::class.java) -> localDevice
                    it.type.isAssignableFrom(Node::class.java) -> localDevice.node
                    else -> error("Unsupported type ${it.type} in entrypoint $entrypoint")
                }
            }.toTypedArray()
        }
        ktfunction.call(*parameters)
    }
}
