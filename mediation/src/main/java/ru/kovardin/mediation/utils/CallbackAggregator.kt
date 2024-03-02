package ru.kovardin.mediation.utils

class CallbackAggregator(val count: Int) {
    var final: (() -> Unit)? = null
    private var calls = 0

    @Synchronized
    fun increment() {
        if (++calls == count)
            final?.invoke()
    }
}