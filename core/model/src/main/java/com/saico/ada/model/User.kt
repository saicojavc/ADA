package com.saico.ada.model

data class User(
    val name: String,
    val isMother: Boolean,
    val occupation: String = ""
)
