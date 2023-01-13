package branch

import aggregate
import kotlin.test.Test
import kotlin.test.assertTrue

class WhenTest {
    @Test
    fun whenSingleExpression(){
        val condition = true
        val x = if(condition) "hello" else 123
        val result = aggregate {
            when (x) {
                is String -> neighbouring("test")
                else -> neighbouring("test")
            }
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("INSTANCEOF") &&
                    it.path.toString().contains("String") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun whenSingleExpressionElseCase(){
        val condition = false
        val x = if(condition) "hello" else 123
        val result = aggregate {
            when (x) {
                is String -> neighbouring("test")
                else -> neighbouring("test")
            }
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("false")
        })
    }

    @Test
    fun whenWithNestedFunction(){
        val condition = true
        val x = if(condition) "hello" else 123

        val result = aggregate {
            fun test() {
                neighbouring("test")
            }
            fun test2() {
                test()
            }

            when (x) {
                is String -> test2()
                else -> test2()
            }
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("INSTANCEOF") &&
                    it.path.toString().contains("String") &&
                    it.path.toString().contains("test") &&
                    it.path.toString().contains("test2") &&
                    it.path.toString().contains("true")
        })
    }
}
