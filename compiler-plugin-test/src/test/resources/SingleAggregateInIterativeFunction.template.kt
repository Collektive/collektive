import it.unibo.collektive.aggregate.api.Aggregate
%(imports)

fun Aggregate<Int>.exampleAggregate(): Int = 0

/**
    Kotlin program template with several template lines: before/after the call, before/after the aggregate code
     aggregate code inside the iterative function and the iterative function itself.
    The parts after "%" are placeholders for string formatting that allow putting an arbitrary
    piece of code inside them (avoiding having to create multiple testing sources).
*/
fun Aggregate<Int>.x() {
    %(beforeCall)
    listOf(1, 2, 3).%(iterativeFunction) {
        %(beforeAggregate)
        %(aggregate)
        %(afterAggregate)
    }
    %(afterCall)
}
