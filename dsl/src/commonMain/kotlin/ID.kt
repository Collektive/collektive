import kotlin.random.Random

interface ID {
    val id: Any?
}

data class IntId(override val id: Any? = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)) : ID
