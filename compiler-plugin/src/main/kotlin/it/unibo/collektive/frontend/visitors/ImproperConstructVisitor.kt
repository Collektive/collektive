/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.visitors

import it.unibo.collektive.frontend.checkers.CheckersUtility.extractReturnExpression
import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.isStructurallyEquivalentTo
import it.unibo.collektive.utils.common.AggregateFunctionNames.EVOLVING_FUNCTION_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.NEIGHBORING_FUNCTION_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.NEIGHBORING_INLINE_FUNCTION_FQ_NAME
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
class ImproperConstructVisitor(private val constructNameFQName: String) : FirVisitorVoid() {
    private var nestingLevel = 0
    private var isReplaceable = false
    private var markExpression = false
    private var isInsideNeighboring = false
    private var neighboringExpression: FirExpression? = null
    private var parametersDeclarations = emptyList<FirBasedSymbol<*>>()
    private var tracedDependentSymbols = emptyList<FirBasedSymbol<*>>()

    /**
     * Simple visitor that returns the explicit receiver expression of a `yielding` call, or `null` if it is not found.
     */
    private class YieldingReceiverVisitor : FirVisitorVoid() {
        private var returnExpression: FirExpression? = null

        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }

        override fun visitFunctionCall(functionCall: FirFunctionCall) {
            if (functionCall.fqName() == YIELDING_FUNCTION_FQ_NAME) {
                returnExpression = functionCall.explicitReceiver
            }
        }

        /**
         * Returns the yielding receiver of a return statement, or `null` if it is not found.
         *
         * Example:
         * ```kotlin
         * return value.max(0).yielding { "example" }
         * ```
         * In this case, the yielding receiver is `value.max(0)`.
         */
        fun FirReturnExpression.getYieldingReceiver(): FirExpression? {
            visitElement(this)
            return returnExpression
        }
    }

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    override fun visitProperty(property: FirProperty) {
        super.visitProperty(property)
        // trace the symbols that depends on the parameters of the construct
        if (markExpression) {
            tracedDependentSymbols += property.symbol
            markExpression = false
        }
    }

    override fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference) {
        // mark the expression if it uses the parameters of the construct or the traced dependent symbols
        if (resolvedNamedReference.resolvedSymbol in parametersDeclarations ||
            resolvedNamedReference.resolvedSymbol in tracedDependentSymbols
        ) {
            markExpression = true
        }
        super.visitResolvedNamedReference(resolvedNamedReference)
    }

    override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression) {
        // extract the parameters only of the top construct call (not the nested anonymous functions)
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
        val functionFqn = functionCall.fqName()
        isInsideNeighboring = functionFqn == NEIGHBORING_FUNCTION_FQ_NAME ||
            functionFqn == NEIGHBORING_INLINE_FUNCTION_FQ_NAME
        val firstArgument = functionCall.argumentList.arguments.firstOrNull()
        if (isInsideNeighboring && firstArgument is FirAnonymousFunctionExpression) {
            // case in which the neighboring construct is used with an anonymous function as parameter
            val neighboringReturn = firstArgument.extractReturnExpression()?.also { visitElement(it) }
            if (markExpression) {
                markExpression = false
                neighboringExpression = neighboringReturn
            }
        } else {
            super.visitFunctionCall(functionCall)
            if (isInsideNeighboring && markExpression) {
                // case in which the neighboring construct is used with a simple expression as parameter
                markExpression = false
                neighboringExpression = firstArgument
            }
        }
        isInsideNeighboring = false
    }

    override fun visitReturnExpression(returnExpression: FirReturnExpression) {
        super.visitReturnExpression(returnExpression)
        if (neighboringExpression != null && nestingLevel == 1) {
            val expressionToCheck = neighboringExpression as FirExpression // needed for null safety
            if (constructNameFQName == EVOLVING_FUNCTION_FQ_NAME) {
                // if the construct is the `evolving` one we check if the yielding receiver is different from the
                // neighboring one
                val yieldingReceiver =
                    with(YieldingReceiverVisitor()) {
                        returnExpression.getYieldingReceiver()
                    } ?: return

                isReplaceable = !yieldingReceiver.isStructurallyEquivalentTo(expressionToCheck)
            } else {
                isReplaceable = !returnExpression.result.isStructurallyEquivalentTo(expressionToCheck)
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
