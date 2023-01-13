package branch

import aggregate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IfElseNestedFunction {

    @Test
    fun alignWithNestedFunction(){
        val result = aggregate {
            val condition = true
            fun test() {
                neighbouring("test")
            }
            fun test2() {
                test()
            }

            if (condition) {
                test2()
            }
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("condition") &&
                    it.path.toString().contains("test") &&
                    it.path.toString().contains("test2") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun notAlignWithNestedFunction(){
        val result = aggregate {
            val condition = true
            fun test(): String {
                return "hello"
            }
            fun test2() {
                test()
            }

            if (condition) {
                test2()
            }
        }
        assertFalse(result.toSend.keys.any {
            it.path.toString().contains("condition") &&
                    it.path.toString().contains("true")
        })
    }
}
