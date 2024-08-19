package it.unibo.collektive.frontend

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.closestNonLocal
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
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

/**
 * Checker that looks for aggregate functions called inside a loop without an explicit align operation.
 */
object NoAlignInsideALoop : FirFunctionCallChecker(MppCheckerKind.Common) {

    private fun CheckerContext.isInsideAlignDeclaration(): Boolean {
        val wrapping = (closestNonLocal as? FirSimpleFunction)?.name?.asString()
        return wrapping == "alignedOn" || wrapping == "align"
    }

    private fun FirFunctionCall.isAggregate(context: CheckerContext): Boolean =
        toResolvedCallableSymbol()
            ?.receiverParameter
            ?.typeRef
            ?.toClassLikeSymbol(context.session)
            ?.name?.asString() == "Aggregate"

    private fun CheckerContext.isInsideALoop(): Boolean = containingElements.any { it is FirWhileLoop }

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val calleeName = expression.calleeReference.name.identifier

        if (expression.isAggregate(context) &&
            context.isInsideALoop() &&
            !context.isInsideAlignDeclaration()
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
 * Extension that adds a series of checkers that looks for missing align operations within the Collektive DSL.
 */
class MissingAlignExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val functionCallCheckers: Set<FirFunctionCallChecker>
            get() = setOf(NoAlignInsideALoop)
    }
}
