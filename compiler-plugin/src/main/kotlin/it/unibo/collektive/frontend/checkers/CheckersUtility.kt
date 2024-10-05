package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.getContainingClassSymbol
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.expressions.unwrapExpression

/**
 * Collection of utilities for FIR checkers.
 */
object CheckersUtility {
    /**
     * Object containing the types of errors/warnings reported by this extension.
     */
    object PluginErrors {
        /**
         * Warning generated on a dot call.
         */
        val DOT_CALL_WARNING by warning1<PsiElement, String>(
            SourceElementPositioningStrategies.CALL_ELEMENT_WITH_DOT,
        )
    }

    /**
     * Checks is a specific receiver parameter is [Aggregate][it.unibo.collektive.aggregate.api.Aggregate]
     * (`Aggregate<ID>.example()`).
     * It uses the [session] of the [CheckerContext] in which this check is performed to get the class symbol of the
     * parameter.
     *
     * It returns **true** if it's an `Aggregate` function, **false** otherwise.
     */
    fun FirReceiverParameter.isAggregate(session: FirSession): Boolean =
        typeRef.toClassLikeSymbol(session)?.name?.asString() == "Aggregate"

    /**
     * Checks if the function that is called is an [Aggregate][it.unibo.collektive.aggregate.api.Aggregate] one
     * (i.e. is an extension of `Aggregate` or it's a method of the `Aggregate` class).
     * It uses the [session] of the [CheckerContext] in which this check is performed to get the class symbol of the
     * receiver parameter.
     *
     * It returns **true** if it's an `Aggregate` function, **false** otherwise.
     */
    fun FirFunctionCall.isAggregate(session: FirSession): Boolean {
        val callableSymbol = toResolvedCallableSymbol()
        return callableSymbol?.receiverParameter?.isAggregate(session) == true ||
            callableSymbol?.getContainingClassSymbol(session)?.name?.asString() == "Aggregate"
    }

    /**
     * Checks if the current [CheckerContext] is wrapped inside an
     * [Aggregate][it.unibo.collektive.aggregate.api.Aggregate] function.
     *
     * It returns **true** if it is wrapped inside an `Aggregate` function, **false** otherwise.
     */
    fun CheckerContext.isInsideAggregateFunction(): Boolean =
        containingElements.any { (it as? FirSimpleFunction)?.receiverParameter?.isAggregate(session) == true }

    /**
     * Returns wrapping [elements][FirElement] until it finds the element that satisfies the predicate (which is
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
     */
    fun CheckerContext.wrappingElementsUntil(
        excludeDotCall: Boolean = true,
        predicate: (FirElement) -> Boolean,
    ): List<FirElement>? {
        val calleeName = (containingElements.last() as? FirFunctionCall)?.functionName()
        return containingElements.takeIf { it.any(predicate) }
            ?.let { firElements ->
                if (excludeDotCall) {
                    firElements.filterNot {
                        calleeName == it.receiverName()
                    }
                } else {
                    firElements
                }
            }
            ?.dropLast(1)
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
     * Returns the receiver [List] only if it doesn't contain any function declaration, or `null` otherwise.
     */
    fun List<FirElement>.discardIfFunctionDeclaration(): List<FirElement>? =
        takeIf { elements -> elements.none { it is FirSimpleFunction } }

    /**
     * Returns the receiver [List] only if it doesn't contain any `aggregate` block.
     * For example, if the List represents the containing elements, in this code:
     * ```kotlin
     * for(...) {
     *    aggregate {
     *       <CONTEXT_ELEMENT>
     *    }
     * }
     * ```
     * the List is *discarded* (i.e. it returns null) because a portion of the containing elements is _outside_ the
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
    fun FirFunctionCall.functionName(): String =
        calleeReference.name.asString()
}
