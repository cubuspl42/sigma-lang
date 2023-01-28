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
