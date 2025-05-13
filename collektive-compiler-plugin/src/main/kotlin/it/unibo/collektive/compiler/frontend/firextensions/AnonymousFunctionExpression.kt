/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Extracts the return expression from an anonymous function, or `null` if it is not found.
 */
internal fun FirAnonymousFunctionExpression.extractReturnExpression(): FirExpression? = object : FirVisitorVoid() {
    private var returnExpression: FirExpression? = null

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    override fun visitReturnExpression(expression: FirReturnExpression) {
        returnExpression = expression.result
    }

    /**
     * Extracts the return expression from the anonymous function.
     */
    fun extractReturnExpression(): FirExpression? {
        visitElement(this@extractReturnExpression)
        return returnExpression
    }
}.extractReturnExpression()
