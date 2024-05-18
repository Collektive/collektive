package it.unibo.collektive.field.operations

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> plus(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() + other.toDouble()) as T
        is Float -> (value.toFloat() + other.toFloat()) as T
        is Long -> (value.toLong() + other.toLong()) as T
        is Int -> (value.toInt() + other.toInt()) as T
        is Short -> (value.toShort() + other.toShort()) as T
        is Byte -> (value.toByte() + other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> minus(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() - other.toDouble()) as T
        is Float -> (value.toFloat() - other.toFloat()) as T
        is Long -> (value.toLong() - other.toLong()) as T
        is Int -> (value.toInt() - other.toInt()) as T
        is Short -> (value.toShort() - other.toShort()) as T
        is Byte -> (value.toByte() - other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> div(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() / other.toDouble()) as T
        is Float -> (value.toFloat() / other.toFloat()) as T
        is Long -> (value.toLong() / other.toLong()) as T
        is Int -> (value.toInt() / other.toInt()) as T
        is Short -> (value.toShort() / other.toShort()) as T
        is Byte -> (value.toByte() / other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> times(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() * other.toDouble()) as T
        is Float -> (value.toFloat() * other.toFloat()) as T
        is Long -> (value.toLong() * other.toLong()) as T
        is Int -> (value.toInt() * other.toInt()) as T
        is Short -> (value.toShort() * other.toShort()) as T
        is Byte -> (value.toByte() * other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> rem(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() % other.toDouble()) as T
        is Float -> (value.toFloat() % other.toFloat()) as T
        is Long -> (value.toLong() % other.toLong()) as T
        is Int -> (value.toInt() % other.toInt()) as T
        is Short -> (value.toShort() % other.toShort()) as T
        is Byte -> (value.toByte() % other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}
