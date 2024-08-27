package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.isAggregate
import it.unibo.collektive.frontend.checkers.CheckersUtility.PluginErrors
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirWhileLoop

/**
 * Checker that looks for aggregate functions called inside a loop without an explicit align operation.
 */
object NoAlignInsideLoop : FirFunctionCallChecker(MppCheckerKind.Common) {

    private fun CheckerContext.isInsideALoopWithoutAlignedOn(): Boolean {
        val loopElementIndex = containingElements.indexOfLast { it is FirWhileLoop }
        val functionDeclarationIndex = containingElements.dropLast(1).indexOfLast {
            (it as? FirSimpleFunction)?.receiverParameter?.isAggregate(session) == true
        }
        if (loopElementIndex == -1 || functionDeclarationIndex == -1) {
            return false
        }
        return loopElementIndex > functionDeclarationIndex &&
                containingElements
                    .drop(loopElementIndex)
                    .dropLast(1)
                    .filterIsInstance<FirFunctionCall>()
                    .map { it.calleeReference.name.asString() }
                    .none { it == "alignedOn" }
    }

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val calleeName = expression.calleeReference.name.identifier
        if (expression.isAggregate(context.session) &&
            context.isInsideALoopWithoutAlignedOn()
        ) {
            reporter.reportOn(
                expression.calleeReference.source,
                PluginErrors.DOT_CALL_WARNING,
                "Warning: aggregate function \"$calleeName\" called inside a loop with no manual alignment operation",
                context,
            )
        }
    }
}
