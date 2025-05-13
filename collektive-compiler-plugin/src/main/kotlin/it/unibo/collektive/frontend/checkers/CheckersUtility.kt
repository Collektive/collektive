/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.utils.common.AggregateFunctionNames.AGGREGATE_CLASS_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.FIELD_CLASS_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.IGNORE_FUNCTION_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.classId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.unwrapExpression
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.references.toResolvedNamedFunctionSymbol
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.providers.firProvider
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

/**
 * Collection of utility functions for FIR-based static checks in the Collektive frontend compiler plugin.
 *
 * This object provides helpers for inspecting the FIR (Frontend Intermediate Representation) tree,
 * particularly in the context of identifying and handling aggregate functions and expressions
 * that interact with aggregate programming constructs.
 */
object CheckersUtility {

    private val aggregateTypes = listOf(AGGREGATE_CLASS_FQ_NAME, FIELD_CLASS_FQ_NAME)

    /**
     * Checks if any of the types in the sequence represents an aggregate type.
     *
     * @return `true` if at least one type in the sequence is an aggregate type, `false` otherwise.
     */
    private fun Sequence<ConeKotlinType>.anyIsAggregate(session: FirSession): Boolean = any { it.isAggregate(session) }

    /**
     * Checks whether the receiver type corresponds to a recognized aggregate type.
     *
     * @receiver a [ConeKotlinType] to analyse
     * @return `true` if the type is an aggregate or field type, `false` otherwise
     */
    fun ConeKotlinType.isAggregate(session: FirSession): Boolean =
        this.classId?.asFqNameString() in aggregateTypes || directSuperTypes(session).any { it.isAggregate(session) }

    /**
     * Retrieves the super types of this [ConeKotlinType] in the context of the given [session].
     */
    fun ConeKotlinType.directSuperTypes(session: FirSession): Set<ConeKotlinType> =
        (toSymbol(session) as? FirClassSymbol)?.resolvedSuperTypes?.toSet().orEmpty()

    /**
     * Recursively retrieves all super types of this [ConeKotlinType] in the context of the given [session].
     *
     * @receiver a [ConeKotlinType] to analyse
     * @param session the current [FirSession]
     * @return a set of all super types of this type
     */
    fun ConeKotlinType.allSuperTypes(session: FirSession): Set<ConeKotlinType> {
        val superTypes = directSuperTypes(session)
        return superTypes + superTypes.flatMap { it.allSuperTypes(session) }
    }

    /**
     * Determines whether a given [FirFunctionCall] involves aggregate-typed receivers or arguments.
     */
    fun FirFunctionCall?.isAggregate(context: CheckerContext): Boolean = when (this) {
        null -> false
        else -> !hasAnnotationDisablingPlugin(context) &&
            this.calleeReference.toResolvedNamedFunctionSymbol().isAggregate(context)
    }

    /**
     * Determines whether this function symbol represents an aggregate function.
     *
     * A function is considered aggregate if:
     * - It is not annotated with any annotation that disables the Collektive plugin.
     * - At least one of its receivers or arguments is of an aggregate-related type.
     *
     * @receiver the [FirFunctionSymbol] to analyze (nullable)
     * @param context the [CheckerContext], used to access the current compilation context
     * @return `true` if the function should be treated as aggregate, `false` otherwise
     */
    @OptIn(SymbolInternals::class)
    fun FirFunctionSymbol<*>?.isAggregate(context: CheckerContext): Boolean = this?.fir.isAggregate(context)

    /**
     * Determines whether a given [FirFunction] involves aggregate-typed receivers or parameters.
     */
    fun FirDeclaration?.isAggregate(context: CheckerContext): Boolean = when (this) {
        null -> false
        else -> !hasAnnotationDisablingPlugin(context) &&
            receiversAndArgumentsTypes().anyIsAggregate(context.session)
    }

    /**
     * Checks whether the current context is enclosed within an aggregate function.
     *
     * @receiver the [CheckerContext] to inspect
     * @return `true` if inside a function whose receiver or parameters are aggregate-typed
     */
    fun CheckerContext.isInsideAggregateFunction(): Boolean =
        containingElements.any { (it as? FirFunction).isAggregate(this) }

    /**
     * Returns wrapping [FirElement]s until it finds the element that satisfies the predicate (which is
     * excluded from the result). The context's element is excluded.
     *
     * If [excludeDotCall] is set to *true*, elements that represent a dot call on the context's element are excluded
     * as well. For example, in this code:
     * ```kotlin
     * for (...) {
     *    for (...) {
     *       <CONTEXT_ELEMENT>.map(...)
     *    }
     * }
     * ```
     * With [excludeDotCall] set to *false*, this method would return `List(FirFunctionCall("map"),
     * FirWhileLoop("for"), FirWhileLoop("for"))`, instead with *true* the first `map` would be excluded.
     *
     * An example of [predicate] is `{ it is FirWhileLoop }`, which makes this method return all the containing
     * elements until it finds the first Kotlin `for (...)` that wraps the context's element. If there is no such loop,
     * this method returns `null`.
     */
    fun CheckerContext.wrappingElementsUntil(
        excludeDotCall: Boolean = true,
        predicate: (FirElement) -> Boolean,
    ): List<FirElement>? {
        val calleeName = (containingElements.last() as? FirFunctionCall)?.functionName()
        return containingElements
            .takeIf { it.any(predicate) }
            ?.let { firElements ->
                if (excludeDotCall) {
                    firElements.filterNot {
                        calleeName == it.receiverName()
                    }
                } else {
                    firElements
                }
            }?.dropLast(1)
            ?.takeLastWhile { !predicate(it) }
    }

    /**
     * Gets the receiver's name of this element. For example, in this code:
     * ```kotlin
     * exampleFunction().map { ... }
     * ```
     * If we call this method on the `map` element, it returns `exampleFunction`.
     * If, instead, we call this method on something that is not a function call or that has no receiver, it returns
     * `null`.
     */
    fun FirElement.receiverName(): String? =
        ((this as? FirFunctionCall)?.explicitReceiver?.unwrapExpression() as? FirFunctionCall)?.functionName()

    /**
     * Returns the receiver list only if it doesn't contain any function declaration, or `null` otherwise.
     */
    fun List<FirElement>.discardIfFunctionDeclaration(): List<FirElement>? =
        takeIf { elements -> elements.none { it is FirSimpleFunction } }

    /**
     * Returns the receiver list only if it doesn't contain any `aggregate` block.
     * For example, if the List represents the containing elements, in this code:
     * ```kotlin
     * for(...) {
     *    aggregate {
     *       <CONTEXT_ELEMENT>
     *    }
     * }
     * ```
     * the list is *discarded* (i.e. it returns null) because a portion of the containing elements is _outside_ the
     * `aggregate` block.
     */
    fun List<FirElement>.discardIfOutsideAggregateEntryPoint(): List<FirElement>? =
        takeIf { it.none(isFunctionCallsWithName("aggregate")) }

    /**
     * Returns a predicate for a [FirElement] that is *true* when that element represents a function call that has the
     * provided [name].
     */
    fun isFunctionCallsWithName(name: String): ((FirElement) -> Boolean) = {
        it is FirFunctionCall && it.functionName() == name
    }

    /**
     * Returns the name of the called function in a [FirFunctionCall] element.
     */
    fun FirFunctionCall.functionName(): String = calleeReference.name.asString()

    /**
     * Returns the fully qualified name of this [FirFunctionCall].
     */
    fun FirFunctionCall.fqName(): String {
        val callableId = calleeReference.toResolvedFunctionSymbol()?.callableId

        val packageName = callableId?.packageName?.asString()
        val className = callableId?.className?.asString()
        val functionName = callableId?.callableName?.asString()
        return if (className != null) {
            "$packageName.$className.$functionName"
        } else {
            "$packageName.$functionName"
        }
    }

    /**
     * Returns the types of receivers and arguments associated with this [FirFunction].
     */
    fun FirDeclaration.receiversAndArgumentsTypes() =
        (symbol as? FirCallableSymbol<*>)?.receiversAndArgumentsTypes().orEmpty()

    /**
     * Returns the types of receivers and arguments associated with this [FirFunctionCall].
     */
    fun FirFunctionCall.receiversAndArgumentsTypes(): Sequence<ConeKotlinType> =
        calleeReference.toResolvedNamedFunctionSymbol()?.receiversAndArgumentsTypes().orEmpty()

    /**
     * Computes the complete list of receiver and argument types for a function symbol.
     *
     * This includes:
     * - context parameters
     * - value parameters
     * - dispatch receiver
     * - extension receiver
     */
    fun FirCallableSymbol<*>.receiversAndArgumentsTypes(): Sequence<ConeKotlinType> {
        val valueParameters: Sequence<ConeKotlinType> = (this as? FirFunctionSymbol<*>)
            ?.valueParameterSymbols?.asSequence()?.map { it.resolvedReturnType }.orEmpty()
        val receiver = sequenceOf(resolvedReceiverTypeRef?.coneType).filterNotNull()
        val dispatchReceiver = sequenceOf(dispatchReceiverType).filterNotNull()
        val contextParameters: Sequence<ConeKotlinType> = resolvedContextParameters.asSequence()
            .map { it.returnTypeRef.coneType }
        return contextParameters + valueParameters + dispatchReceiver + receiver
    }

    /**
     * Checks if the [FirExpression] is structurally equivalent to another [FirExpression].
     */
    fun FirExpression.isStructurallyEquivalentTo(other: FirExpression): Boolean = render() == other.render()

    /**
     * Extracts the return expression from an anonymous function, or `null` if it is not found.
     */
    fun FirAnonymousFunctionExpression.extractReturnExpression(): FirExpression? = object : FirVisitorVoid() {
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
}

/**
 * Checks whether this [FirFunction] or any of its enclosing declarations
 * are annotated with [CollektiveIgnore].
 *
 * Functions or classes marked with [CollektiveIgnore] are excluded
 * from alignment and treated as purely local computations.
 *
 * @param context the [CheckerContext] used for the check
 * @return `true` if the function or a parent is annotated with [CollektiveIgnore]
 */
fun FirAnnotationContainer?.hasAnnotationDisablingPlugin(context: CheckerContext): Boolean = when (this) {
    null -> false
    else -> annotations.any { it.disablesPlugin() } ||
        context.hasAnnotationDisablingPlugin() ||
        when (this) {
            is FirFunction ->
                getContainingClass().hasAnnotationDisablingPlugin(context) ||
                    context.session.firProvider.getFirCallableContainerFile(this.symbol)
                        .hasAnnotationDisablingPlugin(context)
            else -> false
        }
}

/**
 * Checks whether this function symbol or its surrounding context is annotated to disable the Collektive plugin.
 *
 * This function returns `true` if either:
 * - The function itself is annotated with a plugin-disabling annotation, or
 * - Any of the surrounding elements in the given [context] are annotated to disable the plugin.
 *
 * @receiver the [FirFunctionSymbol] to inspect
 * @param context the [CheckerContext] providing access to surrounding FIR elements
 * @return `true` if plugin checks should be disabled, `false` otherwise
 */
fun FirFunctionSymbol<*>.hasAnnotationDisablingPlugin(context: CheckerContext): Boolean =
    annotations.any { it.disablesPlugin() } || context.hasAnnotationDisablingPlugin()

/**
 * Checks whether any of the elements in the current [CheckerContext] contain an annotation
 * that disables the Collektive compiler plugin.
 *
 * This function inspects all [FirAnnotation]s in the [containingElements] and returns `true`
 * if any of them match the disabling annotation defined by [IGNORE_FUNCTION_ANNOTATION_FQ_NAME].
 *
 * @receiver the current [CheckerContext] during FIR analysis
 * @return `true` if plugin execution should be disabled due to annotations, `false` otherwise
 */
fun CheckerContext.hasAnnotationDisablingPlugin(): Boolean = containingElements
    .flatMap { (it as? FirAnnotationContainer)?.annotations.orEmpty() }
    .any { it.disablesPlugin() }

/**
 * Checks whether this annotation disables the Collektive compiler plugin for the annotated function.
 *
 * This returns `true` if the annotation matches the fully qualified name defined in
 * [IGNORE_FUNCTION_ANNOTATION_FQ_NAME], indicating that the function should be excluded
 * from aggregate analysis and plugin checks.
 *
 * @receiver the [FirAnnotation] to inspect
 * @return `true` if the annotation disables the plugin, `false` otherwise
 */
fun FirAnnotation.disablesPlugin() = resolvedType.classId?.asFqNameString() == IGNORE_FUNCTION_ANNOTATION_FQ_NAME

/**
 * TODO: for debugging purposes.
 */
fun FirElement.niceString() = when (this) {
    is FirFunctionCall -> functionName()
    is FirSimpleFunction -> name.asString()
    is FirClassLikeDeclaration -> classId.asString()
    is FirClassSymbol<*> -> classId.asString()
    is FirFile -> name
    else ->
        this::class.simpleName
            ?.removePrefix("Fir")
            ?.removeSuffix("Impl")
            ?.removeSuffix("Expression")
            ?: "Unknown"
}
