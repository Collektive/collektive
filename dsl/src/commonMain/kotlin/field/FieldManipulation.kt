package field

import ID

@Suppress("UNCHECKED_CAST")
fun <T> Field<Comparable<T>>.min(includingSelf: Boolean = true): T =
    handleIncludingSelf(includingSelf).values.reduce { x,y -> if (x <= y as T) x else y } as T

@Suppress("UNCHECKED_CAST")
fun <T> Field<Comparable<T>>.max(includingSelf: Boolean = true): T =
    handleIncludingSelf(includingSelf).values.reduce { x,y -> if (x >= y as T) x else y } as T

private fun <T> Field<Comparable<T>>.handleIncludingSelf(includingSelf: Boolean): Map<ID, Comparable<T>> =
    this.toMap().filterKeys { id -> (!includingSelf && id != local.first) || includingSelf}