package field

import ID

@Suppress("UNCHECKED_CAST")
fun <T : Comparable<T>> Field<T>.min(includingSelf: Boolean = true): T =
    handleIncludingSelf(includingSelf).values.reduce { x,y -> if (x <= y as T) x else y } as T

@Suppress("UNCHECKED_CAST")
fun <T : Comparable<T>> Field<T>.max(includingSelf: Boolean = true): T =
    handleIncludingSelf(includingSelf).values.reduce { x,y -> if (x >= y as T) x else y } as T

private fun <T : Comparable<T>> Field<T>.handleIncludingSelf(includingSelf: Boolean): Map<ID, Comparable<T>> =
    if (includingSelf) this.toMap() else this.excludeSelf()

operator fun Field<Double>.plus(field: Field<Double>): Field<Double> {
    this.toMap().map { (k,v) -> v + (field.toMap()[k] ?: 0.0) }
    return this
}