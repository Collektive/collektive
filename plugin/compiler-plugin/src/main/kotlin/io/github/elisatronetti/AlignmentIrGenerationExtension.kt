package io.github.elisatronetti

import io.github.elisatronetti.utils.common.Name
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.FqName

/**
 * The generation extension is used to register the transformer plugin, which is going to modify
 * the IR using the function responsible for the alignment.
 */
class AlignmentIrGenerationExtension: IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        // Function that is responsible to handle the alignment
        val alignedOnFunction = pluginContext.referenceFunctions(FqName(Name.ALIGNED_ON_FUNCTION_FQ_NAME)).firstOrNull()
        // Aggregate Context class that has the reference to the stack
        val aggregateContextClass = pluginContext.referenceClass(FqName(Name.AGGREGATE_CONTEXT_CLASS))
        if (alignedOnFunction!= null && aggregateContextClass != null) {
            moduleFragment.transform(
                AggregateCallTransformer(
                    pluginContext,
                    aggregateContextClass.owner,
                    alignedOnFunction.owner
                ),
                null
            )
        } else {
            if (alignedOnFunction == null)
                println("[COMPILER-PLUGIN]: the function that is used to handle the alignment has not been found.")
            if (aggregateContextClass == null)
                println("[COMPILER-PLUGIN]: the aggregate context used to update the stack for " +
                        "alignment has not been found.")
        }
    }
}
