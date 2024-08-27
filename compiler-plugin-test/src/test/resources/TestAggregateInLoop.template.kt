import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate(): Int = 0

/**
    Kotlin program template with four template lines: before/after the loop and
    before/after the aggregate call inside the loop.
    %%s is a placeholder for string formatting that allows putting an arbitrary
    piece of code inside it (avoiding having to create multiple testing sources).
*/
fun Aggregate<Int>.x(pivot: (Int) -> Any?) {
    %s
    for (j in listOf(1, 2, 3)) {
        %s
        exampleAggregate()
        %s
    }
    %s
}
