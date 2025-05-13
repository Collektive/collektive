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
 * Visitor that checks if a [FirFunctionCall] contains an unnecessary yielding context. Look at the documentation of
 * [it.unibo.collektive.frontend.checkers.UnnecessaryYielding] for more information.
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
     * Checks if the [FirFunctionCall] contains an unnecessary yielding context. Look at the documentation of
     * [it.unibo.collektive.frontend.checkers.UnnecessaryYielding] for more information.
     */
    fun containsUnnecessaryYielding(functionCall: FirFunctionCall): Boolean {
        visitElement(functionCall)
        return containsUnnecessaryYielding
    }
}
