/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.frontend.checkers.CheckersUtility.isAggregate
import it.unibo.collektive.frontend.checkers.CheckersUtility.isFunctionCallsWithName
import it.unibo.collektive.utils.common.AggregateFunctionNames
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.arguments
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
                // Trailing lambda support
                method.parameters.lastOrNull()?.let { parameter ->
                    parameter.parameterizedType.typeName.startsWith("kotlin.jvm.functions.Function") ||
                        parameter.parameterizedType is Function<*>
                } == true
            }.map { it.name }
            .toSet()
    }

    private fun FirElement.isACallToALoopingCollectionsMethod(): Boolean =
        this is FirFunctionCall && this.functionName() in collectionMembers

    private fun FirElement.isLastArgumentOf(parent: FirElement): Boolean =
        parent is FirFunctionCall && parent.arguments.lastOrNull() == this

    private fun isInsideALoop(child: FirElement, parent: FirElement): Boolean = child is FirLoop ||
        parent is FirLoop ||
        parent.isACallToALoopingCollectionsMethod() &&
        child.isLastArgumentOf(parent)

    private fun FirElement.isAlignedOn() =
        isFunctionCallsWithName(AggregateFunctionNames.ALIGNED_ON_FUNCTION_NAME)(this)

    private fun CheckerContext.isIteratedWithoutAlignedOn(): Boolean {
        val elements = containingElements.zipWithNext()
        val elementsBeforeLoop = elements.takeWhile { (child, parent) -> !isInsideALoop(child, parent) }
        val atLeastOneLoop = elementsBeforeLoop.size < elements.size
        val alignedOn = elementsBeforeLoop.any { (child, _) -> child.isAlignedOn() } ||
            elementsBeforeLoop.lastOrNull()?.second?.isAlignedOn() == true
        return !alignedOn && atLeastOneLoop
    }
//    private fun CheckerContext.isIteratedWithoutAlignedOn(): Boolean =
//        wrappingElementsUntil { isALoopInsideAnAggregateContext(it) }
//            ?.discardIfFunctionDeclaration()
//            ?.discardIfOutsideAggregateEntryPoint()
//            ?.none(isFunctionCallsWithName(AggregateFunctionNames.ALIGNED_ON_FUNCTION_NAME))
//            ?: false

//    private fun isInvalidFunWithAggregateParameter(expression: FirFunctionCall, context: CheckerContext): Boolean {
//        val visitor = FunctionCallWithAggregateParVisitor(context)
//        return visitor.visitSuspiciousFunctionCallDeclaration(expression)
//    }
//
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val calleeName = expression.functionName()
        if (expression.fqName() in safeOperators) return
        val error =
            when {
                expression.isAggregate(context) && context.isIteratedWithoutAlignedOn() ->
                    FirCollektiveErrors.AGGREGATE_FUNCTION_INSIDE_ITERATION
                else -> null
            }
        error?.let {
            reporter.reportOn(expression.calleeReference.source, it, calleeName, context)
        }
    }
}
