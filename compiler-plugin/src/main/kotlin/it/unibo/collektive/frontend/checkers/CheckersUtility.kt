package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
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
     * Checks is a specific receiver parameter is `Aggregate` (`Aggregate<ID>.example()`).
     * @param session [FirSession] session of the [CheckerContext].
     * @return [Boolean] **true** if it's an `Aggregate` function, **false** otherwise.
     */
    fun FirReceiverParameter.isAggregate(session: FirSession): Boolean =
        this.typeRef.toClassLikeSymbol(session)?.name?.asString() == "Aggregate"

    /**
     * Checks if the function that is called is an `Aggregate` one.
     * @param session [FirSession] session of the [CheckerContext].
     * @return [Boolean] **true** if it's an `Aggregate` function, **false** otherwise.
     */
    fun FirFunctionCall.isAggregate(session: FirSession): Boolean =
        toResolvedCallableSymbol()?.receiverParameter?.isAggregate(session) == true

    /**
     * Checks if the current [CheckerContext] is wrapped inside an `Aggregate` function.
     * @return [Boolean] **true** if it is wrapped inside an `Aggregate` function, **false** otherwise.
     */
    fun CheckerContext.isInsideAggregateFunction(): Boolean =
        containingElements.any { (it as? FirSimpleFunction)?.receiverParameter?.isAggregate(session) == true }
}
