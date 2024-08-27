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

    fun FirReceiverParameter.isAggregate(session: FirSession): Boolean =
        this.typeRef.toClassLikeSymbol(session)?.name?.asString() == "Aggregate"

    fun FirFunctionCall.isAggregate(session: FirSession): Boolean =
        toResolvedCallableSymbol()?.receiverParameter?.isAggregate(session) == true

    fun CheckerContext.isInsideAggregateFunction(): Boolean =
        containingElements.any { (it as? FirSimpleFunction)?.receiverParameter?.isAggregate(session) == true }

}
