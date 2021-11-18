package com.example.trailz.ui.common

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <A, B> Iterable<A>.mapAsync(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}


fun <T> Iterable<T>.mapFind(
    predicate: (T) -> Boolean,
    transform: (T) -> T
): List<T> {
    return map {
        if (predicate(it)) transform(it)
        else it
    }
}
