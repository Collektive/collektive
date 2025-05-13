/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.visitors

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirUnitExpression
import org.jetbrains.kotlin.fir.visitors.FirVisitor

/**
 * A visitor that checks if a function call has an empty return (i.e., no return statement,
 * resulting into a [FirUnitExpression] as return expression).
 */
class EmptyReturnVisitor : FirVisitor<Unit, Boolean>() {
    private var hasReturn = true

    override fun visitElement(element: FirElement, data: Boolean) {
        element.acceptChildren(this, data)
    }

    override fun visitReturnExpression(returnExpression: FirReturnExpression, data: Boolean) {
        super.visitReturnExpression(returnExpression, true)
    }

    override fun visitExpression(expression: FirExpression, data: Boolean) {
        if (data && expression is FirUnitExpression) {
            hasReturn = false
        } else {
            super.visitExpression(expression, data)
        }
    }

    /**
     * Check if the function call has an empty return (i.e., no return statement, resulting into a [FirUnitExpression]).
     * Returns `true` if the function call has an empty return, `false` otherwise.
     */
    fun FirFunctionCall.hasEmptyReturn(): Boolean {
        visitElement(this, false)
        return !hasReturn
    }
}
