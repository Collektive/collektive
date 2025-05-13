/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:Suppress("ReturnCount")

package it.unibo.collektive.compiler

import it.unibo.collektive.compiler.backend.transformers.AggregateFunctionTransformer
import it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_FQ_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.ALIGN_FUNCTION_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.DEALIGN_FUNCTION_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.PROJECTION_FUNCTION_NAME
import it.unibo.collektive.compiler.common.error
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * IR generation extension for the Collektive compiler plugin.
 *
 * This extension registers the [AggregateFunctionTransformer], which traverses the IR
 * and injects alignment logic (e.g., `alignRaw` / `dealign`) into aggregate-aware functions.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class CollektiveIrGenerationExtension(private val logger: MessageCollector) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val aggregateClass =
            checkNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(AGGREGATE_CLASS_FQ_NAME)))) {
                "Class $AGGREGATE_CLASS_FQ_NAME not found"
            }
        val fieldClass =
            checkNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(FIELD_CLASS_FQ_NAME)))) {
                "Class $FIELD_CLASS_FQ_NAME not found"
            }
        val getContextSymbol = checkNotNull(fieldClass.getPropertyGetter("context")) {
            "Property 'context' not found in class $FIELD_CLASS_FQ_NAME"
        }
        val projectFunction = pluginContext.referenceFunctions(
            CallableId(
                FqName("it.unibo.collektive.aggregate.api.impl"),
                Name.identifier(PROJECTION_FUNCTION_NAME),
            ),
        ).firstOrNull() ?: return logger.error("Unable to find the 'project' function")
        val alignRawFunction = aggregateClass.getFunctionReferenceWithName(ALIGN_FUNCTION_NAME)
            ?: return logger.error("Unable to find the '$ALIGN_FUNCTION_NAME' function")
        val dealignFunction = aggregateClass.getFunctionReferenceWithName(DEALIGN_FUNCTION_NAME)
            ?: return logger.error("Unable to find the '$DEALIGN_FUNCTION_NAME' function")
        /*
         * Apply the transformation to all aggregate-aware functions in the module.
         */
        moduleFragment.transform(
            AggregateFunctionTransformer(
                pluginContext,
                logger,
                aggregateClass.owner,
                fieldClass.owner,
                alignRawFunction.owner,
                dealignFunction.owner,
                projectFunction.owner,
                getContextSymbol.owner,
            ),
            null,
        )
    }

    private fun IrClassSymbol.getFunctionReferenceWithName(functionName: String): IrFunctionSymbol? =
        functions.single { it.owner.name == Name.identifier(functionName) }
}
