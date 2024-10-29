package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.discardIfFunctionDeclaration
import it.unibo.collektive.frontend.checkers.CheckersUtility.discardIfOutsideAggregateEntryPoint
import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.frontend.checkers.CheckersUtility.isAggregate
import it.unibo.collektive.frontend.checkers.CheckersUtility.isFunctionCallsWithName
import it.unibo.collektive.frontend.checkers.CheckersUtility.wrappingElementsUntil
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirWhileLoop

/**
 * Checker that looks for aggregate functions called inside a loop without an explicit align operation.
 */
object NoAlignInsideLoop : FirFunctionCallChecker(MppCheckerKind.Common) {

    private val safeOperators = listOf(
        "it.unibo.collektive.aggregate.api.Aggregate.alignedOn",
        "it.unibo.collektive.aggregate.api.Aggregate.align",
        "it.unibo.collektive.aggregate.api.Aggregate.dealign",
    )

    /**
     * Methods used inside collections to iterate their elements.
     */
    private val collectionMembers = listOf(
        Class.forName("kotlin.collections.CollectionsKt"),
        Collection::class.java,
        Iterable::class.java,
        List::class.java,
        Map::class.java,
        Sequence::class.java,
        Set::class.java
    )
        .flatMap { it.methods.toList() }
        .filter { method ->
            method.parameters.any { parameter ->
                parameter.parameterizedType.typeName.startsWith("kotlin.jvm.functions.Function") ||
                        parameter.parameterizedType is Function<*>
            }
        }
        .map { it.name }
        .toSet()

    private fun CheckerContext.isInsideALoopWithoutAlignedOn(): Boolean =
        wrappingElementsUntil { it is FirWhileLoop }
            ?.discardIfFunctionDeclaration()
            ?.discardIfOutsideAggregateEntryPoint()
            ?.none(isFunctionCallsWithName("alignedOn")) ?: false

    private fun CheckerContext.isInsideIteratedFunctionWithoutAlignedOn(): Boolean =
        wrappingElementsUntil { it is FirFunctionCall && it.functionName() in collectionMembers }
            ?.discardIfFunctionDeclaration()
            ?.discardIfOutsideAggregateEntryPoint()
            ?.none(isFunctionCallsWithName("alignedOn")) ?: false

    private fun CheckerContext.isIteratedWithoutAlignedOn(): Boolean =
        isInsideALoopWithoutAlignedOn() || isInsideIteratedFunctionWithoutAlignedOn()

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val calleeName = expression.functionName()
        if (expression.fqName() !in safeOperators &&
            expression.isAggregate(context.session) &&
            context.isIteratedWithoutAlignedOn()
        ) {
            reporter.reportOn(
                expression.calleeReference.source,
                CheckersUtility.PluginErrors.DOT_CALL_WARNING,
                "Warning: aggregate function '$calleeName' called inside a loop without explicit alignment",
                context,
            )
        }
    }
}
