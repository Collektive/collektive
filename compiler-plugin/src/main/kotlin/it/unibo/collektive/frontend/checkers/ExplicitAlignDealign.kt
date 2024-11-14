package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.isInsideAggregateFunction
import it.unibo.collektive.utils.common.AggregateFunctionNames
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
        val fqnCalleeName = expression.fqName()
        if (fqnCalleeName in FORBIDDEN_FUNCTIONS && context.isInsideAggregateFunction()) {
            reporter.reportOn(
                expression.calleeReference.source,
                FirCollektiveErrors.FORBIDDEN_FUNCTION,
                fqnCalleeName,
                context,
            )
        }
    }

    private val FORBIDDEN_FUNCTIONS = listOf(
        AggregateFunctionNames.ALIGNED_ON_FUNCTION_FQ_NAME,
        AggregateFunctionNames.DEALIGN_FUNCTION_FQ_NAME,
    )
}
