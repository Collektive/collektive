package it.unibo.collektive.frontend

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
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
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol


object PluginErrors {
    val METHOD_CALLED by warning1<PsiElement, String>(SourceElementPositioningStrategies.CALL_ELEMENT_WITH_DOT)
}

object AlignRawCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

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

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter
    ) {
        val calleeName = expression.calleeReference.name.identifier

        if (calleeName == "exampleAggregate") {
            throw IllegalArgumentException("CANT DO THIS BABY DOLL")
        }

        if (expression.isAggregate(context)
            && !context.isInsideAlignDeclaration()) {
//            reporter.reportOn(
//                expression.calleeReference.source,
//                PluginErrors.METHOD_CALLED,
//                "Warning: aggregate function called inside a loop with no manual alignment operation",
//                context
//            )
        }
    }
}

class AlignRawCallExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val functionCallCheckers: Set<FirFunctionCallChecker>
            get() = setOf(AlignRawCallChecker)
    }
}
