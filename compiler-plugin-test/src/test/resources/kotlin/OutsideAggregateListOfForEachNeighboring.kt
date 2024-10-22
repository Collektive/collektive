/**
 * This file has been auto-generated. See 
 * [https://github.com/FreshMag/collektive-plugin/blob/test/generated-cases/compiler-plugin-test/src/test/resources/yaml/TestingCases.yaml](this link).  
 */
import it.unibo.collektive.aggregate.api.Aggregate

import it.unibo.collektive.Collektive.Companion.aggregate

fun entry() {
  listOf(1,2,3).forEach {
    aggregate(0) {
      neighboring(0)
    }
  }
}