@file:Suppress("ReturnCount")

package it.unibo.collektive

import it.unibo.collektive.backend.transformers.AggregateCallTransformer
import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.common.AggregateFunctionNames.ALIGN_FUNCTION_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.DEALIGN_FUNCTION_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.PROJECT_FUNCTION
import it.unibo.collektive.utils.logging.error
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * The generation extension is used to register the transformer plugin, which is going to modify
 * the IR using the function responsible for the alignment.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class AlignmentIrGenerationExtension(
    private val logger: MessageCollector,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        // Aggregate Context class that has the reference to the stack
        val aggregateClass =
            pluginContext.referenceClass(
                ClassId.topLevel(FqName(AggregateFunctionNames.AGGREGATE_CLASS_FQ_NAME)),
            )
        if (aggregateClass == null) {
            return logger.error("Unable to find the aggregate class")
        }

        val projectFunction =
            pluginContext
                .referenceFunctions(
                    CallableId(
                        FqName("it.unibo.collektive.aggregate.api.impl"),
                        Name.identifier(PROJECT_FUNCTION),
                    ),
                ).firstOrNull() ?: return logger.error("Unable to find the 'project' function")

        // Function that handles the alignment
        val alignRawFunction =
            aggregateClass.getFunctionReferenceWithName(ALIGN_FUNCTION_NAME)
                ?: return logger.error("Unable to find the '$ALIGN_FUNCTION_NAME' function")

        val dealignFunction =
            aggregateClass.getFunctionReferenceWithName(DEALIGN_FUNCTION_NAME)
                ?: return logger.error("Unable to find the '$DEALIGN_FUNCTION_NAME' function")

        /*
         This applies the alignment call on all the aggregate functions
         */
        moduleFragment.transform(
            AggregateCallTransformer(
                pluginContext,
                logger,
                aggregateClass.owner,
                alignRawFunction.owner,
                dealignFunction.owner,
                projectFunction.owner,
            ),
            null,
        )
    }

    private fun IrClassSymbol.getFunctionReferenceWithName(functionName: String): IrFunctionSymbol? =
        functions.firstOrNull { it.owner.name == Name.identifier(functionName) }
}
