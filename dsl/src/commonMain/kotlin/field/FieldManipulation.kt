package field

@Suppress("UNCHECKED_CAST")
fun <T> Field<Comparable<T>>.min(includingSelf: Boolean = true): T =
    this.toMap().filterKeys { id -> (!includingSelf && id != local.first) || includingSelf}
        .values.reduce { x,y -> if (x <= y as T) x else y } as T
