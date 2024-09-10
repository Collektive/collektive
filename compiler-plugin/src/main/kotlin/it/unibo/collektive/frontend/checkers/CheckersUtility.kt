package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.getContainingClassSymbol
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol

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
}
