package com.etronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConcat
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrSyntheticBodyImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class TransformerImpl(
    private val pluginContext: IrPluginContext,
    private val logFunction: IrSimpleFunctionSymbol,
) : IrElementTransformerVoid() {

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val body = declaration.body
        // Take only the function declarations
        if (body != null && body !is IrSyntheticBodyImpl && !declaration.name.isSpecial) {
            declaration.body = irDebug(declaration, body)
        }
        return super.visitSimpleFunction(declaration)
    }

    private fun irDebug(
        function: IrFunction,
        body: IrBody
    ): IrBlockBody {
        return DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
            +irEnter(function)
            for (statement in body.statements) +statement
        }
    }

    private fun IrBuilderWithScope.irEnter(
        function: IrFunction
    ): IrFunctionAccessExpression {
        val concat = irConcat()
        concat.addArgument(irString("${function.name} declaration"))
        return irCall(logFunction).also { call ->
            call.putValueArgument(0, concat)
        }
    }
}