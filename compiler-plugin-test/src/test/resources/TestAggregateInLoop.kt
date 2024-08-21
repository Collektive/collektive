import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate(): Int = 0

fun Aggregate<Int>.x(pivot: (Int) -> Any?) {
    %s
    for (j in listOf(1, 2, 3)) {
        %s
        exampleAggregate()
        %s
    }
    %s
}
