/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.visitors

import it.unibo.collektive.compiler.common.CollektiveNames.YIELDING_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.frontend.firextensions.fqName
import it.unibo.collektive.compiler.frontend.firextensions.isStructurallyEquivalentTo
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Visitor that detects unnecessary yielding contexts in a [FirFunctionCall].
 *
 * A yielding context is considered unnecessary if the value passed to `yielding` is
 * structurally identical to the receiver — meaning no transformation occurred.
 *
 * See also [it.unibo.collektive.compiler.frontend.checkers.UnnecessaryYielding].
 */
class UnnecessaryYieldingVisitor : FirVisitorVoid() {

    private var containsUnnecessaryYielding = false
    private var yieldingReceiver: FirExpression? = null
    private var insideYielding = false

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    override fun visitFunctionCall(functionCall: FirFunctionCall) {
        if (functionCall.fqName == YIELDING_FUNCTION_FQ_NAME) {
            insideYielding = true
            yieldingReceiver = functionCall.explicitReceiver
            functionCall.argumentList.arguments.forEach(::visitElement)
            insideYielding = false
            return
        }
        super.visitFunctionCall(functionCall)
    }

    override fun visitReturnExpression(returnExpression: FirReturnExpression) {
        if (insideYielding) {
            val receiverExpression = yieldingReceiver ?: return // needed for null safety
            containsUnnecessaryYielding = returnExpression.result.isStructurallyEquivalentTo(receiverExpression)
            return
        }
        super.visitReturnExpression(returnExpression)
    }

    /**
     * Checks whether the given [functionCall] contains an unnecessary yielding block.
     *
     * @param functionCall the [FirFunctionCall] to inspect
     * @return `true` if the yielded result is structurally identical to the receiver; `false` otherwise
     */
    fun containsUnnecessaryYielding(functionCall: FirFunctionCall): Boolean {
        visitElement(functionCall)
        return containsUnnecessaryYielding
    }
}
