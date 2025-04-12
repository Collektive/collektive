/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.discardIfFunctionDeclaration
import it.unibo.collektive.frontend.checkers.CheckersUtility.discardIfOutsideAggregateEntryPoint
import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.frontend.checkers.CheckersUtility.hasAggregateArgument
import it.unibo.collektive.frontend.checkers.CheckersUtility.isAggregate
import it.unibo.collektive.frontend.checkers.CheckersUtility.isFunctionCallsWithName
import it.unibo.collektive.frontend.checkers.CheckersUtility.isInsideAggregateFunction
import it.unibo.collektive.frontend.checkers.CheckersUtility.wrappingElementsUntil
import it.unibo.collektive.frontend.visitors.FunctionCallWithAggregateParVisitor
import it.unibo.collektive.utils.common.AggregateFunctionNames
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirWhileLoop
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

/**
 * Checker that looks for aggregate functions called inside a loop without an explicit align operation.
 */
object NoAlignInsideLoop : FirFunctionCallChecker(MppCheckerKind.Common) {
    private val safeOperators =
        listOf(
            AggregateFunctionNames.ALIGNED_ON_FUNCTION_FQ_NAME,
            AggregateFunctionNames.ALIGN_FUNCTION_FQ_NAME,
            AggregateFunctionNames.DEALIGN_FUNCTION_FQ_NAME,
        )

    /**
     * Getter for all Collection members using Kotlin reflection, obtaining their names as a set.
     */
    @Deprecated(
        """
        This method currently raises an exception.
        See https://youtrack.jetbrains.com/issue/KT-16479 for more details.
        """,
    )
    @Suppress("UnusedPrivateMember")
    private fun getCollectionMembersKotlin(): Set<String> = sequenceOf(
        Class.forName("kotlin.collections.CollectionsKt").kotlin,
        Collection::class,
        Iterable::class,
        List::class,
        Map::class,
        Sequence::class,
        Set::class,
    ).flatMap { clazz -> clazz.java.methods.mapNotNull { it.kotlinFunction } + clazz.members }
        .filter {
            fun KParameter.isFunctionType(): Boolean = (type.classifier as? KClass<*>)
                ?.qualifiedName
                ?.startsWith("kotlin.Function")
                ?: false
            it.parameters.any { parameter ->
                parameter.isFunctionType()
            }
        }.map { it.name }
        .toSet()

    /**
     * Methods used inside collections to iterate their elements.
     */
    private val collectionMembers: Set<String> by lazy {
        sequenceOf(
            Class.forName("kotlin.collections.CollectionsKt"),
            Collection::class.java,
            Iterable::class.java,
            List::class.java,
            Map::class.java,
            Sequence::class.java,
            Set::class.java,
        ).flatMap { it.methods.asSequence() }
            .filter { method ->
                method.parameters.any { parameter ->
                    parameter.parameterizedType.typeName.startsWith("kotlin.jvm.functions.Function") ||
                        parameter.parameterizedType is Function<*>
                }
            }.map { it.name }
            .toSet()
    }

    private fun FirElement.isACallToALoopingCollectionsMethod(): Boolean =
        this is FirFunctionCall && this.functionName() in collectionMembers

    private fun CheckerContext.isALoopInsideAnAggregateContext(element: FirElement): Boolean =
        (element is FirWhileLoop || element.isACallToALoopingCollectionsMethod()) && isInsideAggregateFunction()

    private fun CheckerContext.isIteratedWithoutAlignedOn(): Boolean =
        wrappingElementsUntil { isALoopInsideAnAggregateContext(it) }
            ?.discardIfFunctionDeclaration()
            ?.discardIfOutsideAggregateEntryPoint()
            ?.none(isFunctionCallsWithName(AggregateFunctionNames.ALIGNED_ON_FUNCTION_NAME))
            ?: false

    private fun isInvalidFunWithAggregateParameter(expression: FirFunctionCall, context: CheckerContext): Boolean {
        val visitor = FunctionCallWithAggregateParVisitor(context)
        return visitor.visitSuspiciousFunctionCallDeclaration(expression)
    }

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val calleeName = expression.functionName()
        if (expression.fqName() in safeOperators) return
        val error =
            when {
                expression.isAggregate(context.session) && context.isIteratedWithoutAlignedOn() ->
                    FirCollektiveErrors.AGGREGATE_FUNCTION_INSIDE_ITERATION

                expression.hasAggregateArgument() &&
                    context.isIteratedWithoutAlignedOn() &&
                    isInvalidFunWithAggregateParameter(expression, context) ->
                    FirCollektiveErrors.FUNCTION_WITH_AGGREGATE_PARAMETER_INSIDE_ITERATION

                else -> null
            }
        error?.let {
            reporter.reportOn(expression.calleeReference.source, it, calleeName, context)
        }
    }
}
