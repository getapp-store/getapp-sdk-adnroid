package ru.kovardin.mediation.interfaces


interface InitializedCallbacks {
    fun onInitialized(network: String)
}

interface MediationCallbacks : InitializedCallbacks {
    fun onFinish()
}