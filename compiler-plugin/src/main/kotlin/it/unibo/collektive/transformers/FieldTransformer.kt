package it.unibo.collektive.transformers

import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.common.isAssignableFrom
import it.unibo.collektive.utils.logging.debug
import it.unibo.collektive.visitors.collectAggregateReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

/**
 * Transforms the generated IR only when an operation on a field is involved:
 * for each field operation inside the `alignedOn` function and inside the bodies of a branch,
 * the field is wrapped in the `project` function.
 */
class FieldTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateClass: IrClass,
    private val projectFunction: IrFunction,
) : IrElementTransformerVoid() {
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall): IrExpression {
        val symbolName = expression.symbol.owner.name
        val alignRawIdentifier = Name.identifier(AggregateFunctionNames.ALIGN_FUNCTION_NAME)
        val alignedOnIdentifier = Name.identifier(AggregateFunctionNames.ALIGNED_ON_FUNCTION_NAME)
        if (symbolName == alignRawIdentifier || symbolName == alignedOnIdentifier) {
            logger.debug("Found alignedRaw function call: ${expression.dumpKotlinLike()}")
            val contextReference =
                expression.receiverAndArgs()
                    .find { it.type.isAssignableFrom(aggregateClass.defaultType) }
                    ?: collectAggregateReference(aggregateClass, expression.symbol.owner)
            contextReference?.let {
                // If the expression contains a lambda, this recursion is necessary to visit the children
                expression.transformChildren(this, null)
                return expression.transform(
                    FieldProjectionTransformer(pluginContext, projectFunction, it),
                    null,
                )
            }
        }
        return super.visitCall(expression)
    }

    override fun visitBranch(branch: IrBranch): IrBranch {
        val contextReference = collectAggregateReference(aggregateClass, branch.result)
        contextReference?.let {
            logger.debug("Found AggregateContext reference in branch: ${it.type.classFqName}")
            branch.result.transform(this, null)
            return branch.transform(
                FieldProjectionTransformer(pluginContext, projectFunction, it),
                null,
            )
        }
        return super.visitBranch(branch)
    }

    override fun visitElseBranch(branch: IrElseBranch): IrElseBranch {
        val contextReference = collectAggregateReference(aggregateClass, branch.result)
        contextReference?.let {
            logger.debug("Found AggregateContext reference in else branch: ${it.type.classFqName}")
            branch.result.transform(this, null)
            return branch.transform(
                FieldProjectionTransformer(pluginContext, projectFunction, it),
                null,
            )
        }
        return super.visitElseBranch(branch)
    }
}
