package io.github.elisatronetti.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

fun <T : IrElement> irStatement(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrSimpleFunction,
    expression: IrElement,
    body: IrSingleStatementBuilder.() -> T
): T =
    IrSingleStatementBuilder(
        pluginContext,
        Scope(aggregateLambdaBody.symbol),
        expression.startOffset,
        expression.endOffset
    ).build(body)