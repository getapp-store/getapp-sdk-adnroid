package ru.kovardin.mediation.models

data class Unit(
    val name: String,
    val unit: String,
    val network: String,
    var placement: Int,
)