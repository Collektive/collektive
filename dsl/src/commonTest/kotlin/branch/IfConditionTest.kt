package branch

import aggregate
import kotlin.test.Test
import kotlin.test.assertTrue

class IfConditionTest {

    @Test
    fun constantConditionIf() {
        val result = aggregate {
             if (true) neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("constant") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun variableConditionIf() {
        val customCondition = true
        val result = aggregate {
            if (customCondition) neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun functionConditionIf() {
        fun customCondition() = true
        val result = aggregate {
            if (customCondition()) neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun functionAndConditionIf() {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate {
            if (customCondition1 && customCondition2) neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition1") &&
                    it.path.toString().contains("customCondition2") &&
                    it.path.toString().contains("AND") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun functionOrConditionIf() {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate {
            if (customCondition1 || customCondition2) neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition1") &&
                    it.path.toString().contains("customCondition2") &&
                    it.path.toString().contains("OR") &&
                    it.path.toString().contains("true")
        })
    }

    @Test
    fun functionNotConditionIf() {
        val customCondition1 = true
        val customCondition2 = false
        val result = aggregate {
            if (customCondition1 && !customCondition2) neighbouring("test")
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("customCondition1") &&
                    it.path.toString().contains("customCondition2") &&
                    it.path.toString().contains("AND") &&
                    it.path.toString().contains("not") &&
                    it.path.toString().contains("true")
        })
    }
}
