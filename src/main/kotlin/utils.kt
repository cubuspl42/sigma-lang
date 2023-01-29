fun getResourceAsText(
    path: String,
): String? = object {}.javaClass.getResource(path)?.readText()

fun <T> List<T>.indexOfOrNull(
    element: T,
): Int? = when (val index = this.indexOf(element)) {
    -1 -> null
    else -> index
}

fun <A> List<A>.indexOfOrNull(
    predicate: (A) -> Boolean,
): Int? = this.withIndex().firstOrNull { predicate(it.value) }?.index

inline fun <K, V, R : Any> Map<K, V>.mapKeysNotNull(transform: (Map.Entry<K, V>) -> R?): Map<R, V> {
    return mapKeysNotNullTo(LinkedHashMap(), transform)
}

inline fun <K, V, R : Any> Map<K, V>.mapKeysNotNullTo(
    destination: MutableMap<R, V>,
    transform: (Map.Entry<K, V>) -> R?,
): Map<R, V> {
    forEach { element -> transform(element)?.let { destination.put(it, element.value) } }
    return destination
}

data class CutList<E>(
    val front: List<E>,
    val tail: List<E>?,
)

fun <E> List<E>.cutOffFront(n: Int): CutList<E>? {
    if (this.size < n) return null

    if (this.size == n) return CutList(
        front = this,
        tail = null,
    )

    return CutList(
        front = this.take(n),
        tail = this.drop(n),
    )
}
