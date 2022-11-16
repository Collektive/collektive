package com.etronetti

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.FqName

class IrGenerationExtensionImpl: IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val typeAnyNullable = pluginContext.irBuiltIns.anyNType

        val funPrintln = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
            .single {
                val parameters = it.owner.valueParameters
                parameters.size == 1 && parameters[0].type == typeAnyNullable
            }

        moduleFragment.transform(TransformerImpl(pluginContext, funPrintln), null)
    }
}