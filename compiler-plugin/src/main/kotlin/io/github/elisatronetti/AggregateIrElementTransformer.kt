package io.github.elisatronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * Looking for the aggregate function call, find all its function call
 * childern and transform them.
 */
class AlignmentIrElementTransformer(
    private val pluginContext: IrPluginContext,
    private val stampa: IrFunction,
    private val aggregateContext: IrClass
) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        if (expression.symbol.owner.name.asString() == "aggregate") {
            val calls = collectCalls(expression)
            for (call in calls) {
                call.transform(ModifyAlignTrasformer(pluginContext, stampa, aggregateContext), null)
            }
        }
        //val aggregateReceiver = expression.receiverAndArgs().find { aggregateContext.contains(it) }
        /*if (aggregateReceiver != null) {
            //println(expression.dump())
            //println("---------------")
        }*/

        /*aggregateReceiver.forEach {
            println( it.dump())
        }
        println("-----------------")*/

        /*if (aggregateReceiver != null) {
            // IrExpression devono essere le tre call in fila
            // aggregateReceiver.alignRaw(expression.symbol.owner.name).let { call() }.also { dealign() }
            irAlign(aggregateReceiver, expression.symbol.owner., )
            // chiama align passandogli la funzione che sto per chiamare
        }*/
        return super.visitCall(expression)
    }
}
