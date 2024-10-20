package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.discardIfFunctionDeclaration
import it.unibo.collektive.frontend.checkers.CheckersUtility.discardIfOutsideAggregateEntryPoint
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
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

    private val SAFE_OPERATORS = listOf(
        "it.unibo.collektive.aggregate.api.Aggregate.alignedOn",
        "it.unibo.collektive.aggregate.api.Aggregate.align",
        "it.unibo.collektive.aggregate.api.Aggregate.dealign"
    )

    /**
     * Methods used inside collections to iterate their elements.
     */
    private val ITERATIVE_METHODS = setOf(
        "forEach",
        "filter",
        "map",
        "flatMap",
        "joinToString",
        "contains",
        "last",
        "binarySearch",
        "dropLastWhile",
        "findLast",
        "find",
        "first",
        "foldRight",
        "fold",
        "foldRightIndexed",
        "indexOfFirst",
        "indexOfLast",
        "lastOrNull",
        "mapIndexed",
        "all",
        "any",
        "mapNotNull",
        "filterNot",
        "filterIndexed",
        "filterNotNull",
        "none",
        "forEachIndexed",
        "reduce",
        "reduceIndexed",
        "foldIndexed",
        "groupBy",
        "associateBy",
        "partition",
        "takeWhile",
        "dropWhile",
        "sortedBy",
        "sortedWith",
        "associate",
        "associateByTo",
        "associateTo",
        "associateWith",
        "associateWithTo",
        "chunked",
        "containsAll",
        "count",
        "distinctBy",
        "elementAtOrElse",
        "filterNotTo",
        "filterTo",
        "firstNotNullOf",
        "firstNotNullOfOrNull",
        "firstOrNull",
        "flatMapIndexed",
        "flatMapIndexedTo",
        "flatMapTo",
        "groupByTo",
        "lastIndexOf",
        "mapIndexedNotNull",
        "mapIndexedNotNullTo",
        "mapIndexedTo",
        "mapNotNullTo",
        "mapTo",
        "maxBy",
        "maxByOrNull",
        "maxOf",
        "maxOfOrNull",
        "maxOfWith",
        "maxOfWithOrNull",
        "maxWith",
        "maxWithOrNull",
        "maxOrNull",
        "minBy",
        "minByOrNull",
        "minOf",
        "minOfOrNull",
        "minOfWith",
        "minOfWithOrNull",
        "minWith",
        "minWithOrNull",
        "minOrNull",
        "reduceIndexedOrNull",
        "reduceOrNull",
        "runningFold",
        "run",
        "runningFoldIndexed",
        "runningReduce",
        "runningReduceIndexed",
        "scan",
        "scanIndexed",
        "single",
        "singleOrNull",
        "sortedByDescending",
        "sumOf",
        "zip",
        "zipWithNext",
        "onEach",
        "onEachIndexed",
        "takeIf",
        "takeUnless",
        "closure",
        "convert",
        "filterIsInstanceAnd",
        "filterIsInstanceAndTo",
        "filterIsInstanceMapNotNull",
        "filterIsInstanceMapNotNullTo",
        "filterIsInstanceMapTo",
        "filterIsInstanceWithChecker",
        "filterToSetOrEmpty",
        "findIsInstanceAnd",
        "firstNotNullResult",
        "flatMapToNullable",
        "flatMapToNullableSet",
        "foldMap",
        "joinToWithBuffer",
        "keysToMap",
        "keysToMapExceptNulls",
        "mapToSetOrEmpty",
        "memoryOptimizedFilter",
        "memoryOptimizedFilterIsInstance",
        "memoryOptimizedFilterNot",
        "memoryOptimizedFlatMap",
        "memoryOptimizedMap",
        "memoryOptimizedMapIndexed",
        "memoryOptimizedMapNotNull ",
        "memoryOptimizedZip",
        "same",
        "selectMostSpecificInEachOverridableGroup",
        "sumByLong",
        "sure",
    )

    private fun CheckerContext.isInsideALoopWithoutAlignedOn(): Boolean =
        wrappingElementsUntil { it is FirWhileLoop }
            ?.discardIfFunctionDeclaration()
            ?.discardIfOutsideAggregateEntryPoint()
            ?.none(isFunctionCallsWithName("alignedOn")) ?: false

    private fun CheckerContext.isInsideIteratedFunctionWithoutAlignedOn(): Boolean =
        wrappingElementsUntil { it is FirFunctionCall && it.functionName() in ITERATIVE_METHODS }
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
        if (expression.fqName() !in SAFE_OPERATORS &&
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
