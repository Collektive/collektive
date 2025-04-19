package it.unibo.collektive.collektivize.utils

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty

/**
 * Utilities for interoperate with kotlin poet.
 */
object KotlinPoetUtils {
    private fun KCallable<*>.isProperty() = this is KProperty<*>

    /**
     * Generate the body for function where the extension receiver is a `Field` and the function has no arguments.
     */
    internal fun FunSpec.Builder.addSingleStatementBodyFunction(callable: KCallable<*>) {
        val baseCall = "return·%N·{·it.${callable.name}"
        when {
            callable.isProperty() -> addStatement("$baseCall·}", FIELD_MAP)
            callable.typeParameters.isEmpty() -> addStatement("$baseCall()·}", FIELD_MAP)
            else -> addStatement("$baseCall<${callable.typeParameters.joinToString { it.name }}>()·}", FIELD_MAP)
        }
    }

    /**
     * Generate the body for function where the extension receiver is a `Field`.
     * The strategy is to call the `mapWithId` function on the receiver and use the `id` on arguments that are `Field`,
     * and the argument itself otherwise.
     */
    internal fun FunSpec.Builder.addBodyFunction(
        callable: KCallable<*>,
        parameters: List<ParameterSpec>,
        isReceiverField: Boolean,
    ) {
        val functionParameters = parameters.drop(1)
        val candidateParameter = functionParameters.firstOrNull { it.isField() }
        val firstFieldParameter = parameters.first { it.isField() }
        val arguments =
            functionParameters.map {
                when (it.type.toString().contains("Field")) {
                    true -> "${it.name}[id]"
                    false -> it.name
                }
            }
        val namedParameter = if (arguments.any { it.contains("id") }) "id" else "_"
        val toCheckAligned = parameters.filter { it.isField() }
        if (toCheckAligned.size > 1) {
            addStatement("%M(${toCheckAligned.joinToString(separator = ",·") { it.name }})", CHECK_ALIGNED)
        }
        when (callable.isProperty()) {
            true ->
                when (isReceiverField) {
                    true ->
                        addStatement(
                            "return ${firstFieldParameter.name}.%N·{·($namedParameter,·receiver)·->·receiver.%N }",
                            FIELD_MAP_WITH_ID,
                            callable.name,
                        )
                    false ->
                        addStatement(
                            "return ${candidateParameter?.name}.%N·{·($namedParameter,·receiver)·->·this.%N }",
                            FIELD_MAP_WITH_ID,
                            callable.name,
                        )
                }

            else ->
                when (isReceiverField) {
                    true ->
                        addStatement(
                            "return ${firstFieldParameter.name}.%N·{·($namedParameter,·receiver)·->·receiver.%N(${
                                arguments.joinToString(
                                    separator = ",·",
                                )
                            })·}",
                            FIELD_MAP_WITH_ID,
                            callable.name,
                        )
                    false ->
                        addStatement(
                            "return ${candidateParameter?.name}.%N·{·($namedParameter,·receiver)·->·this.%N(${
                                arguments.joinToString(
                                    separator = ",·",
                                )
                            })·}",
                            FIELD_MAP_WITH_ID,
                            callable.name,
                        )
                }
        }
    }
}
