import kotlin.random.Random

interface ID

data class IntId(val id: Any? = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)) : ID
