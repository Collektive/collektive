class Aggregate {
    // nbr
    fun neighbouring() = println("neighbouring")
    // rep
    fun repeating() = println("repeating")
    // share
    fun sharing() = println("sharing")
}

fun aggregate(init: Aggregate.() -> Unit): Aggregate = Aggregate().apply(init)
