package it.unibo.collektive.transformers

import it.unibo.collektive.utils.logging.info
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * This transformer replaces the body of the function `isEnabledFunction` with a return statement that returns `true`.
 */
class EnabledCompilerPluginTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val isEnabledFunction: IrFunction,
) : IrElementTransformerVoid() {

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.name == isEnabledFunction.name) {
            logger.info("Transforming the function ${declaration.name} to return `true` because the plugin is enabled.")
            declaration.body = DeclarationIrBuilder(pluginContext, declaration.symbol).irBlockBody {
                +irReturn(irBoolean(true))
            }
            return declaration
        }
        return super.visitFunction(declaration)
    }
}
