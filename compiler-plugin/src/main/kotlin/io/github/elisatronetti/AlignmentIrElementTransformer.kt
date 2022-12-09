package io.github.elisatronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrSyntheticBodyImpl
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * This transform the generated IR, creating in the function declaration a new function call,
 * which is responsible to handle the alignment.
 */
class AlignmentIrElementTransformer(
    private val pluginContext: IrPluginContext,
    private val alignOnFunction: IrFunction,
    private val aggregateContext: IrClass
) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        val declaration = expression.symbol.owner // Declaration of the function call
        val body = declaration.body
        if (body != null && hasToBeStacked(declaration)) {
            // Create new function declaration body
            declaration.body = irAlign(declaration, body)
        }
        return super.visitCall(expression)
    }

    private fun irAlign(
        function: IrFunction,
        body: IrBody
    ): IrBlockBody {
        return DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
            // Add alignment call at the beginning of the function
            +irEnter(function)
            // Put back all the function statements
            for (statement in body.statements) +statement
        }
    }

    private fun IrBuilderWithScope.irEnter(
        function: IrFunction
    ): IrFunctionAccessExpression {
        val concat = irConcat()
        concat.addArgument(irString("${function.name}"))
        // Create function call with the name of the function as argument
        return irCall(alignOnFunction).also { call ->
            //call.extensionReceiver = TODO()
            call.putValueArgument(0, concat)
        }
    }

    private fun hasToBeStacked(declaration: IrSimpleFunction): Boolean {
        // Function that has not to be modified
        return (declaration.body !is IrSyntheticBodyImpl &&
                !declaration.name.isSpecial)
    }
}
