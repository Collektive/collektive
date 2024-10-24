/**
 * This file has been auto-generated. See 
 * [https://github.com/FreshMag/collektive-plugin/blob/test/generated-cases/compiler-plugin-test/src/test/resources/yaml/TestingCases.yaml](this link).  
 */
import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.entry() {
    alignedOn(0) {
        listOf(1,2,3).forEach {
            neighboring(0)
        }
    }
}