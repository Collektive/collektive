/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.visitors

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Visitor that checks for usages of value parameters inside an anonymous function call block.
 */
class ConstructCallVisitor : FirVisitorVoid() {
    private var checkedParametersDeclarations = listOf<FirValueParameterSymbol>()
    private var found = true
    private var nestedAnonymousFunction = false

    override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
    }

    override fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference) {
        if (resolvedNamedReference.resolvedSymbol in checkedParametersDeclarations) {
            checkedParametersDeclarations =
                checkedParametersDeclarations.filter { it != resolvedNamedReference.resolvedSymbol }
            if (checkedParametersDeclarations.isEmpty()) {
                found = true
            }
        }
    }

    override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression) {
        if (!nestedAnonymousFunction) {
            found = false
            val anonymousFunction = anonymousFunctionExpression.anonymousFunction
            val parameters = anonymousFunction.valueParameters
            checkedParametersDeclarations = parameters.map { it.symbol }
            if (checkedParametersDeclarations.isEmpty()) {
                found = true
                return
            }
            nestedAnonymousFunction = true
        }
        super.visitAnonymousFunctionExpression(anonymousFunctionExpression)
    }

    /**
     * Checks for usages, inside an anonymous function call block, of its value parameters. This includes
     * implicit parameters (i.e. `it`). Returns `true` if any of the value parameters is used, `false` otherwise.
     */
    fun FirFunctionCall.doesNotContainValueParameterUsagesInAnonymousFunctionCall(): Boolean {
        visitElement(this)
        return !found
    }
}
