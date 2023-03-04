package field

import ID

fun <T : Comparable<T>> Field<T>.min(includingSelf: Boolean = true): Map.Entry<ID, T>? =
    handleIncludingSelf(includingSelf).minByOrNull { it.value }

fun <T : Comparable<T>> Field<T>.max(includingSelf: Boolean = true): Map.Entry<ID, T>? =
    handleIncludingSelf(includingSelf).maxByOrNull { it.value }

private fun <T : Comparable<T>> Field<T>.handleIncludingSelf(includingSelf: Boolean): Map<ID, T> =
    if (includingSelf) this.toMap() else this.excludeSelf()

operator fun Field<Double>.plus(field: Field<Double>): Field<Double> {
    val res = (this.toMap().toList() + field.toMap().toList())
        .groupBy({ it.first }, { it.second })
        .map { (key, values) -> key to values.sum() }
        .toMap()
    return FieldImpl(this.localId, res)
}
