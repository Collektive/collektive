package it.unibo.collektive.frontend

import it.unibo.collektive.frontend.checkers.ExplicitAlignDealign
import it.unibo.collektive.frontend.checkers.NoAlignInsideLoop
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

/**
 * Extension that adds a series of checkers that looks for missing align operations within the Collektive DSL.
 */
class MissingAlignExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers =
        object : ExpressionCheckers() {
            override val functionCallCheckers: Set<FirFunctionCallChecker>
                get() = setOf(NoAlignInsideLoop, ExplicitAlignDealign)
        }
}
