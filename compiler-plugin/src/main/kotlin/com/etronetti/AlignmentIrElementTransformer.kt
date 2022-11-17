package com.etronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrSyntheticBodyImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class TransformerImpl(
    private val pluginContext: IrPluginContext,
    private val stampa: IrFunction
) : IrElementTransformerVoid() {

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val body = declaration.body
        // Take only the function declarations
        if (body != null && body !is IrSyntheticBodyImpl && !declaration.name.isSpecial && declaration.name.toString() != "addToken" && declaration.name.toString() != "alignedOn" && declaration.name.toString() != "hashCode" && declaration.name.toString() != "currentPath" && declaration.name.toString() != "toString" && declaration.name.toString() != "clearStack" && declaration.name.toString() != "equals" && declaration.name.toString() != "copy") {
            println(declaration.name)
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
        concat.addArgument(irString("${function.name}"))
        println(stampa.dump())
        return irCall(stampa).also {call ->
            call.putValueArgument(0, concat)
        }
    }
}