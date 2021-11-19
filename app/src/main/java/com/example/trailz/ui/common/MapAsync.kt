package com.example.trailz.ui.common

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun <T> Iterable<T>.mapFind(
    predicate: (T) -> Boolean,
    transform: (T) -> T
): List<T> {
    return map {
        if (predicate(it)) transform(it)
        else it
    }
}
