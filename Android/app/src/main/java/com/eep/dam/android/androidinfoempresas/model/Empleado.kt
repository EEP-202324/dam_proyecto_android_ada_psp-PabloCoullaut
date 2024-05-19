package com.eep.dam.android.androidinfoempresas.model

data class Empleado(
    val id: Long,
    val nombre: String,
    val cargo: String,
    val empresa: InfoEmpresas
)