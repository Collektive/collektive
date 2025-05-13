/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.irextensions

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.defaultType

/**
 * Builds an [IrFunctionAccessExpression] that accesses the `context` property of a
 * [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME].
 *
 * This utility constructs an IR call to the `context` getter on an [IrGetValue]
 * referencing a [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME] instance,
 * returning the corresponding [it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_NAME] context.
 *
 * @receiver an [IrGetValue] referencing a [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME]
 * @param pluginContext the IR plugin context used to construct the IR
 * @param aggregateClass the IR class representing the `Aggregate<ID>` interface
 * @param fieldClass the IR class representing the `Field<ID, *>` interface
 * @param getContext the IR function symbol corresponding to the `context` property getter
 * @return an [IrFunctionAccessExpression] that retrieves the field's context
 * @throws IllegalStateException if the receiver is not assignable from
 *   [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME]
 */
internal fun IrGetValue.buildGetFieldContext(
    pluginContext: IrPluginContext,
    aggregateClass: IrClass,
    fieldClass: IrClass,
    getContext: IrFunction,
): IrFunctionAccessExpression {
    check(type.isAssignableFrom(fieldClass.defaultType)) {
        "Expected a Field, but got a: ${type.classFqName}"
    }
    return IrSingleStatementBuilder(pluginContext, Scope(getContext.symbol), startOffset, endOffset)
        .build {
            irCall(getContext.symbol, aggregateClass.defaultType)
                .apply { dispatchReceiver = this@buildGetFieldContext }
        }
}
