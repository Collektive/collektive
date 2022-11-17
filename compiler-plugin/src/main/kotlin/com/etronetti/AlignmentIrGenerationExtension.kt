package com.etronetti

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class IrGenerationExtensionImpl: IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        // Function that is responsible to handle the alignment
        val alignOnFunction = collect(moduleFragment).first()
        moduleFragment.transform(TransformerImpl(pluginContext, alignOnFunction), null)
    }
}
