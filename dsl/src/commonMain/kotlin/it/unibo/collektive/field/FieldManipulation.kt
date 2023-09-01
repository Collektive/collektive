package it.unibo.collektive.field

import it.unibo.collektive.ID

fun <T : Comparable<T>> Field<T>.min(includingSelf: Boolean = true): Map.Entry<ID, T>? =
    handle(includingSelf).minByOrNull { it.value }

fun <T : Comparable<T>> Field<T>.max(includingSelf: Boolean = true): Map.Entry<ID, T>? =
    handle(includingSelf).maxByOrNull { it.value }

private fun <T : Comparable<T>> Field<T>.handle(includingSelf: Boolean): Map<ID, T> =
    if (includingSelf) this.toMap() else this.excludeSelf()

operator fun Field<Double>.plus(field: Field<Double>): Field<Double> {
    val complete = (this.keys + field.keys).associateWith { setOf(this[it], field[it]).filterNotNull().sum() }
    val local = complete.filter { it.key == localId }.values.first()
    val other = complete.filterNot { it.key == localId }
    return Field(localId, local, other)
}
