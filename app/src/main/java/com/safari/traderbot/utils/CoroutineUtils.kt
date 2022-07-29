package com.safari.traderbot.utils

import kotlinx.coroutines.*

fun CoroutineScope.launchPeriodic(
    repeatMillis: Long,
    dispatcher: CoroutineDispatcher,
    action: suspend () -> Unit
) = this.launch(dispatcher) {
    while (isActive) {
        action()
        delay(repeatMillis)
    }
}