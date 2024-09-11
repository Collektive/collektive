package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.PluginErrors
import it.unibo.collektive.frontend.checkers.CheckersUtility.isInsideAggregateFunction
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker that looks for usages of "align" and "dealign" methods inside Aggregate functions, generating a warning.
 */
object ExplicitAlignDealign : FirFunctionCallChecker(MppCheckerKind.Common) {
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val calleeName = expression.calleeReference.name.identifier
        if ((calleeName == "align" || calleeName == "dealign") && context.isInsideAggregateFunction()) {
            reporter.reportOn(
                expression.calleeReference.source,
                PluginErrors.DOT_CALL_WARNING,
                "Warning: '$calleeName' method should not be explicitly used",
                context,
            )
        }
    }
}
