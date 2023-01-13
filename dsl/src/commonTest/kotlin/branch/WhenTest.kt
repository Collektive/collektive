package branch

/*
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
}
 */