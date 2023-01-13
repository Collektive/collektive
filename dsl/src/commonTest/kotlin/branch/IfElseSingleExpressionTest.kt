package branch

/*
import aggregate
import kotlin.test.Test
import kotlin.test.assertTrue

class IfElseSingleExpressionTest {

    @Test
    fun trueConditionIfElseBlock() {
        val customCondition = true
        val result = aggregate {
            if (customCondition) neighbouring("test") else neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun falseConditionIfElseBlock() {
        val customCondition = true
        val result = aggregate {
            if (customCondition) neighbouring("test") else neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition") &&
                    it.path.toString().contains("false")
        })
    }
}
*/