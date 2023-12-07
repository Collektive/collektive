package it.unibo.collektive

import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.logging.error
import it.unibo.collektive.utils.logging.warn
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.functions
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
        val aggregateContextClass = pluginContext.referenceClass(
            ClassId.topLevel(FqName(AggregateFunctionNames.AGGREGATE_CONTEXT_CLASS)),
        )

        // Function that handles the alignment
        val alignedOnFunction = aggregateContextClass
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
        val (alignFunc, aggCtxClass) = getBothOrNull(alignedOnFunction, aggregateContextClass)
            ?: return logger.error(
                "The function and the class used to handle the alignment have not been found.",
            )

        moduleFragment.transform(
            AggregateCallTransformer(pluginContext, logger, aggCtxClass.owner, alignFunc.owner),
            null,
        )
    }

    private fun <F, S> getBothOrNull(first: F?, second: S?): Pair<F, S>? =
        if (first != null && second != null) first to second else null
}
