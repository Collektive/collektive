/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.visitors

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.hasAggregateArgument
import it.unibo.collektive.frontend.checkers.CheckersUtility.isAggregate
import it.unibo.collektive.utils.common.AggregateFunctionNames.ALIGNED_ON_FUNCTION_FQ_NAME
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

class FunctionCallWithAggregateParVisitor(private val context: CheckerContext) : FirVisitorVoid() {

    var found = false
    private var insideAlignedOn = false
    private var functionCounter = 0
    private val insideNestedFun
        get() = functionCounter > 1

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    private fun isInsideAlignedOnOrNestedFun(): Boolean =
        insideAlignedOn || insideNestedFun

    override fun visitFunctionCall(functionCall: FirFunctionCall) {
        if (functionCall.isAggregate(context.session)) {
            if (functionCall.fqName() == ALIGNED_ON_FUNCTION_FQ_NAME) {
                insideAlignedOn = true
                functionCall.acceptChildren(this)
                insideAlignedOn = false
            } else if (!isInsideAlignedOnOrNestedFun()) {
                found = true
                return
            }
        } else if (functionCall.hasAggregateArgument()) {
            val visitor = FunctionCallWithAggregateParVisitor(context)
            found = visitor.visitSuspiciousFunctionCallDeclaration(functionCall)
        }
        return
    }

    override fun visitSimpleFunction(simpleFunction: FirSimpleFunction) {
        functionCounter++
        simpleFunction.body?.accept(this)
        functionCounter--
        return
    }

    @OptIn(SymbolInternals::class)
    fun visitSuspiciousFunctionCallDeclaration(call: FirFunctionCall): Boolean {
        (call.calleeReference.toResolvedFunctionSymbol()?.fir as? FirSimpleFunction)?.accept(this)
        return this.found
    }
}
