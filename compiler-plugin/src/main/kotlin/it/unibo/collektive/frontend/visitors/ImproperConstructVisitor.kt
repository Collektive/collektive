/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.visitors

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.isStructurallyEquivalentTo
import it.unibo.collektive.utils.common.AggregateFunctionNames.EVOLVING_FUNCTION_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.NEIGHBORING_FUNCTION_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.YIELDING_FUNCTION_FQ_NAME
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Visitor that checks if an `evolve` or `evolving` construct can be replaced with a `share` construct.
 */
class ImproperConstructVisitor(
    private val constructNameFQName: String,
) : FirVisitorVoid() {
    private var nestingLevel = 0
    private var isReplaceable = false
    private var markExpression = false
    private var isInsideNeighboring = false
    private var neighboringExpression: FirExpression? = null
    private var parametersDeclarations = emptyList<FirBasedSymbol<*>>()
    private var tracedDependentSymbols = emptyList<FirBasedSymbol<*>>()

    /**
     * Simple visitor that returns the yielding expression of a return statement, or `null` if it is not found.
     */
    private class YieldingCallVisitor : FirVisitorVoid() {
        private var returnExpression: FirExpression? = null
        private var insideYielding = false

        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }

        override fun visitFunctionCall(functionCall: FirFunctionCall) {
            if (functionCall.fqName() == YIELDING_FUNCTION_FQ_NAME) {
                insideYielding = true
                super.visitFunctionCall(functionCall)
                insideYielding = false
            }
        }

        override fun visitReturnExpression(returnExpression: FirReturnExpression) {
            if (insideYielding) {
                this.returnExpression = returnExpression.result
            }
        }

        /**
         * Returns the yielding expression of a return statement, or `null` if it is not found.
         *
         * Example:
         * ```kotlin
         * return value.yielding { "example" }
         * ```
         * In this case, the yielding expression is `"example"`.
         */
        fun FirReturnExpression.getYieldingExpression(): FirExpression? {
            visitElement(this)
            return returnExpression
        }
    }

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
            if (constructNameFQName == EVOLVING_FUNCTION_FQ_NAME) {
                // if the construct is the `evolving` one we check if the yielding expression is different from the
                // neighboring one
                val yieldingExpression =
                    with(YieldingCallVisitor()) {
                        returnExpression.getYieldingExpression()
                    }
                isReplaceable = yieldingExpression?.isStructurallyEquivalentTo(neighboringExpression!!) == false
            } else {
                isReplaceable = returnExpression.result.isStructurallyEquivalentTo(neighboringExpression!!) == false
            }
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
