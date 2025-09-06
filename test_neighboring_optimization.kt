/*
 * Test file to verify neighboring constant optimization
 */
package it.unibo.collektive.test

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.mapNeighborhood

fun testNeighboringWithConstant(): Aggregate<Int>.() -> Unit = {
    // This should be optimized to mapNeighborhood { 42 }
    val field1 = neighboring(42)
    
    // This should be optimized to mapNeighborhood { "hello" }
    val field2 = neighboring("hello")
    
    // This should be optimized to mapNeighborhood { true }
    val field3 = neighboring(true)
    
    // This should NOT be optimized since it's not a constant
    val variable = 42
    val field4 = neighboring(variable)
    
    // This should NOT be optimized since it's a function call
    val field5 = neighboring(getConstant())
}

fun getConstant(): Int = 42