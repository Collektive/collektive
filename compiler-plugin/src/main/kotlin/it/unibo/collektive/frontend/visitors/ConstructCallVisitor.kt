/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.visitors

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Visitor that checks for usages of value parameters inside an anonymous function call block.
 */
class ConstructCallVisitor : FirVisitorVoid() {
    private var checkedParametersNames = listOf<String>()
    private var found = true

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    override fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference) {
        if (resolvedNamedReference.name.asString() in checkedParametersNames) {
            checkedParametersNames = checkedParametersNames.filter { it != resolvedNamedReference.name.asString() }
            if (checkedParametersNames.isEmpty()) {
                found = true
            }
        }
    }

    override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression) {
        val anonymousFunction = anonymousFunctionExpression.anonymousFunction
        val parameters = anonymousFunction.valueParameters
        checkedParametersNames = parameters.map { it.name.asString() }
        if (checkedParametersNames.isEmpty()) {
            found = true
            return
        }
        super.visitAnonymousFunctionExpression(anonymousFunctionExpression)
    }

    /**
     * Checks for usages, inside an anonymous function call block, of its value parameters. This includes
     * implicit parameters (i.e. `it`). Returns `true` if any of the value parameters is used, `false` otherwise.
     */
    fun checkValueParameterUsagesInsideAnonymousFunctionCall(functionCall: FirFunctionCall): Boolean {
        visitElement(functionCall)
        return found
    }
}
