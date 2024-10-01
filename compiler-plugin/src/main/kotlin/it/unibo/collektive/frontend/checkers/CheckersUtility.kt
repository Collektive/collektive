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

    fun CheckerContext.wrappingElementsUntil(
        excludeDotCall: Boolean = true,
        predicate: (FirElement) -> Boolean
    ): List<FirElement>? =
    // these elements are the ones wrapping the context, between the context and the element that satisfies the
    // predicate.
        // The context's element is discarded
        containingElements.takeIf { it.any(predicate) }
            ?.let { firElements ->
                if (excludeDotCall) {
                    firElements.filterNot {
                        (this.containingElements.last() as? FirFunctionCall)?.functionName() == it.receiverName()
                    }
                } else {
                    firElements
                }
            }
            ?.dropLast(1)
            ?.takeLastWhile { !predicate(it) }


    fun FirElement.receiverName(): String? =
        ((this as? FirFunctionCall)?.explicitReceiver?.unwrapExpression() as? FirFunctionCall)?.functionName()

    fun List<FirElement>.filterFunctionDeclarations(): List<FirElement>? =
        takeIf { elements -> elements.none { it is FirSimpleFunction } }

    fun isFunctionCallsWithName(name: String): ((FirElement) -> Boolean) = { el ->
        el is FirFunctionCall && el.calleeReference.name.asString() == name
    }

    fun FirFunctionCall.functionName(): String =
        calleeReference.name.asString()
}
