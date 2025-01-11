/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.visitors

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.utils.common.AggregateFunctionNames.NEIGHBORING_FUNCTION_FQ_NAME
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Visitor that checks if an `evolve` or `evolving` construct can be replaced with a `share` construct.
 */
class ImproperConstructVisitor : FirVisitorVoid() {
    private var nestingLevel = 0
    private var isReplaceable = false
    private var markExpression = false
    private var isInsideNeighboring = false
    private var neighboringExpression: FirExpression? = null
    private var parametersDeclarations = emptyList<FirBasedSymbol<*>>()
    private var tracedDependentSymbols = emptyList<FirBasedSymbol<*>>()

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    override fun visitProperty(property: FirProperty) {
        super.visitProperty(property)
        if (markExpression) {
            tracedDependentSymbols += property.symbol
            markExpression = false
        }
    }

    override fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference) {
        if (resolvedNamedReference.resolvedSymbol in parametersDeclarations ||
            resolvedNamedReference.resolvedSymbol in tracedDependentSymbols
        ) {
            markExpression = true
        }
        super.visitResolvedNamedReference(resolvedNamedReference)
    }

    override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression) {
        if (nestingLevel == 0) {
            val anonymousFunction = anonymousFunctionExpression.anonymousFunction
            val parameters = anonymousFunction.valueParameters
            parametersDeclarations = parameters.map { it.symbol }
        }
        nestingLevel++
        super.visitAnonymousFunctionExpression(anonymousFunctionExpression)
        nestingLevel--
    }

    override fun visitFunctionCall(functionCall: FirFunctionCall) {
        isInsideNeighboring = functionCall.fqName() == NEIGHBORING_FUNCTION_FQ_NAME
        super.visitFunctionCall(functionCall)
        if (isInsideNeighboring && markExpression) {
            markExpression = false
            neighboringExpression = functionCall.argumentList.arguments.first()
        }
        isInsideNeighboring = functionCall.fqName() != NEIGHBORING_FUNCTION_FQ_NAME
    }

    override fun visitReturnExpression(returnExpression: FirReturnExpression) {
        super.visitReturnExpression(returnExpression)
        if (neighboringExpression != null && nestingLevel == 1) {
            isReplaceable = returnExpression.result.render() != neighboringExpression?.render()
            return
        }
    }

    /**
     * Checks if this function call is an `evolve` or `evolving` construct that can be replaced with a `share`
     * construct.
     */
    fun FirFunctionCall.isReplaceableWithShare(): Boolean {
        visitElement(this)
        return isReplaceable
    }
}
