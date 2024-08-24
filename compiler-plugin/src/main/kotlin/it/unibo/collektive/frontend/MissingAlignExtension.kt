package it.unibo.collektive.frontend

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirWhileLoop
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol

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

private fun FirReceiverParameter.isAggregate(session: FirSession): Boolean =
    this.typeRef.toClassLikeSymbol(session)?.name?.asString() == "Aggregate"

private fun FirFunctionCall.isAggregate(session: FirSession): Boolean =
    toResolvedCallableSymbol()?.receiverParameter?.isAggregate(session) == true

private fun CheckerContext.isInsideAggregateFunction(): Boolean =
    containingElements.any { (it as? FirSimpleFunction)?.receiverParameter?.isAggregate(session) == true }

/**
 * Checker that looks for aggregate functions called inside a loop without an explicit align operation.
 */
object NoAlignInsideALoop : FirFunctionCallChecker(MppCheckerKind.Common) {

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

/**
 * Checker that looks for usages of "align" and "dealign" methods inside Aggregate functions, generating a warning.
 */
object NoAlignOrDealign : FirFunctionCallChecker(MppCheckerKind.Common) {
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val calleeName = expression.calleeReference.name.identifier
        if ((calleeName == "align" || calleeName == "dealign") && context.isInsideAggregateFunction()) {
            reporter.reportOn(
                expression.calleeReference.source,
                PluginErrors.DOT_CALL_WARNING,
                "Warning: \"%s\" method should not be explicitly used".format(calleeName),
                context,
            )
        }
    }
}

/**
 * Extension that adds a series of checkers that looks for missing align operations within the Collektive DSL.
 */
class MissingAlignExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val functionCallCheckers: Set<FirFunctionCallChecker>
            get() = setOf(NoAlignInsideALoop, NoAlignOrDealign)
    }
}
