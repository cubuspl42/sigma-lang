package sigma.expressions

inline fun <K, V, R : Any> Map<K, V>.mapKeysNotNull(transform: (Map.Entry<K, V>) -> R?): Map<R, V> {
    return mapKeysNotNullTo(LinkedHashMap(), transform)
}

inline fun <K, V, R : Any> Map<K, V>.mapKeysNotNullTo(destination: MutableMap<R, V>, transform: (Map.Entry<K, V>) -> R?): Map<R, V> {
    forEach { element -> transform(element)?.let { destination.put(it, element.value) } }
    return destination
}