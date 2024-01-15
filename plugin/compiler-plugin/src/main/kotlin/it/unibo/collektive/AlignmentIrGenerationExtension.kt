package it.unibo.collektive

import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.common.AggregateFunctionNames.PROJECT_FUNCTION
import it.unibo.collektive.utils.logging.error
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * The generation extension is used to register the transformer plugin, which is going to modify
 * the IR using the function responsible for the alignment.
 */
class AlignmentIrGenerationExtension(private val logger: MessageCollector) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        // Aggregate Context class that has the reference to the stack
        val aggregateClass = pluginContext.referenceClass(
            ClassId.topLevel(FqName(AggregateFunctionNames.AGGREGATE_CLASS)),
        )

        val projectFunction = pluginContext.referenceFunctions(
            CallableId(
                FqName("it.unibo.collektive.aggregate.api.impl"),
                Name.identifier(PROJECT_FUNCTION)
            )
        ).firstOrNull() ?: return logger.error("Unable to find the 'project' function")

        // Function that handles the alignment
        val alignedOnFunction = aggregateClass
            ?.functions
            ?.filter { it.owner.name == Name.identifier(AggregateFunctionNames.ALIGNED_ON_FUNCTION) }
            ?.firstOrNull()
        requireNotNull(alignedOnFunction) {
            val error = """
                Aggregate alignment requires function ${AggregateFunctionNames.ALIGNED_ON_FUNCTION} to be available.
                Please, add the required library TODO TODO (gradle block):
            """.trimIndent()
            error.also(logger::error)
        }
        val (alignFunction, aggClass) = getBothOrNull(alignedOnFunction, aggregateClass)
            ?: return logger.error(
                "The function and the class used to handle the alignment have not been found.",
            )

        moduleFragment.transform(
            AggregateCallTransformer(
                pluginContext,
                logger,
                aggClass.owner,
                alignFunction.owner,
                projectFunction.owner,
            ),
            null,
        )
    }

    private fun <F, S> getBothOrNull(first: F?, second: S?): Pair<F, S>? =
        if (first != null && second != null) first to second else null
}
