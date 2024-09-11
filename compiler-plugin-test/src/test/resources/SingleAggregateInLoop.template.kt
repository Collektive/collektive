import it.unibo.collektive.aggregate.api.Aggregate
%(imports)

fun Aggregate<Int>.exampleAggregate(): Int = 0

/**
    Kotlin program template with four template lines: before/after the loop and
    before/after the aggregate call inside the loop.
    The parts after "%" are placeholders for string formatting that allow putting an arbitrary
    piece of code inside them (avoiding having to create multiple testing sources).
*/
fun Aggregate<Int>.x() {
    %(beforeLoop)
    for (j in listOf(1, 2, 3)) {
        %(beforeMainCode)
        %(mainCode)
        %(afterMainCode)
    }
    %(afterLoop)
}
