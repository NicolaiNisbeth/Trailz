package com.dtu.trailz.ui.common

fun <T> Iterable<T>.mapFind(
    predicate: (T) -> Boolean,
    transform: (T) -> T
): List<T> {
    return map {
        if (predicate(it)) transform(it)
        else it
    }
}
