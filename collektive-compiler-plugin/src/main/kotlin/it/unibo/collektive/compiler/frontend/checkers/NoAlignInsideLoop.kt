/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames
import it.unibo.collektive.compiler.frontend.CollektiveFrontendErrors
import it.unibo.collektive.compiler.frontend.firextensions.allSuperTypes
import it.unibo.collektive.compiler.frontend.firextensions.fqName
import it.unibo.collektive.compiler.frontend.firextensions.functionName
import it.unibo.collektive.compiler.frontend.firextensions.isAggregate
import it.unibo.collektive.compiler.frontend.firextensions.isAlignedOn
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirArgumentList
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.allReceiverExpressions
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.kotlinFunction

/**
 * Checker that reports aggregate function calls made inside a loop or collection
 * iteration without an explicit `alignedOn` operation.
 */
object NoAlignInsideLoop : FirFunctionCallChecker(MppCheckerKind.Common) {

    /** Functions that are explicitly safe and should not trigger this checker. */
    private val safeOperators =
        listOf(
            CollektiveNames.ALIGNED_ON_FUNCTION_FQ_NAME,
            CollektiveNames.ALIGN_FUNCTION_FQ_NAME,
            CollektiveNames.DEALIGN_FUNCTION_FQ_NAME,
        )

    /** Kotlin collection extension methods used to identify looping constructs. */
    private val kotlinCollectionsExtensions: Set<KFunction<*>> =
        sequenceOf("collections.CollectionsKt", "sequences.SequencesKt")
            .map { Class.forName("kotlin.$it") }
            .flatMap { it.methods.asSequence() }
            .mapNotNull { it.kotlinFunction }
            .toSet()

    /** Reflection-based collection targets that imply iteration. */
    private val collectionsTargetClasses = sequenceOf(
        Collection::class,
        IntRange::class,
        Iterable::class,
        List::class,
        Map::class,
        Sequence::class,
        Set::class,
    ).flatMap { it.functions }.toSet()

    /**
     * Maps each collection method name to its set of receiver type names.
     * Used to match against known looping constructs.
     */
    private val collectionMembers: Map<String, Set<String>> by lazy {
        val allCandidates = collectionsTargetClasses + kotlinCollectionsExtensions
        allCandidates
            .filter { function ->
                function.parameters.size > 1 &&
                    function.parameters.last().type.typeName.let {
                        it.startsWith("kotlin.Function") || it.startsWith("java.util.function.")
                    }
            }
            .map { it.name to it.parameters.first().type.typeName }
            .groupBy { it.first }
            .mapValues { (_, value) -> value.map { it.second }.toSet() }
    }

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val calleeName = expression.functionName
        if (expression.fqName in safeOperators) return
        val error = when {
            expression.isAggregate(context) && context.isIteratedWithoutAlignedOn() ->
                CollektiveFrontendErrors.AGGREGATE_FUNCTION_INSIDE_ITERATION
            else -> null
        }
        error?.let {
            reporter.reportOn(expression.calleeReference.source, it, calleeName, context)
        }
    }

    /**
     * Determines whether the current context represents an iteration
     * lacking an explicit alignment boundary.
     */
    private fun CheckerContext.isIteratedWithoutAlignedOn(): Boolean {
        val outermostAggregateDeclaration = containingDeclarations.firstOrNull { it.isAggregate(this) }
        val elements = containingElements
            .takeLastWhile {
                it !is FirReturnExpression && it !is FirSimpleFunction && it != outermostAggregateDeclaration
            }

        val scanner = elements.toMutableList()
        var iteratedWithoutAlignedOn = false
        while (scanner.isNotEmpty() && !iteratedWithoutAlignedOn) {
            val next = scanner.removeLast()
            when {
                next.isAlignedOn() -> return false
                next is FirLoop -> iteratedWithoutAlignedOn = true
                next is FirArgumentList -> Unit
                scanner.size >= 2 -> {
                    val argumentList = scanner.last() as? FirArgumentList
                    if (argumentList != null && argumentList.arguments.last().source == next.source) {
                        val functionCall = scanner[scanner.size - 2]
                        if (functionCall.isACallToALoopingCollectionsMethod(session)) {
                            iteratedWithoutAlignedOn = true
                        }
                    }
                }
            }
        }
        return iteratedWithoutAlignedOn
    }

    /**
     * Checks if this [FirElement] is a call to a known looping collection method.
     */
    private fun FirElement.isACallToALoopingCollectionsMethod(session: FirSession): Boolean = this is FirFunctionCall &&
        allReceiverExpressions.any { receiver ->
            val type = receiver.resolvedType
            val typeString = type.classId?.asFqNameString()
            val supportedTypes = collectionMembers[functionName].orEmpty()
            typeString in supportedTypes ||
                type.allSuperTypes(session).any { it.classId?.asFqNameString() in supportedTypes }
        }

    /** Gets the fully qualified name of the type represented by this [KType]. */
    private val KType.typeName get() = (classifier as? KClass<*>)?.qualifiedName.orEmpty()
}
