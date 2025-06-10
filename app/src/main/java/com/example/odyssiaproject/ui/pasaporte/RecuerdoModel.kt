package com.example.odyssiaproject.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import com.example.odyssiaproject.R

data class Recuerdo(
    val nombrePais: String,
    val idImagen: Int,
    var offset: MutableState<Offset> = mutableStateOf(Offset(0f, 0f))
)

val recuerdosDisponibles = listOf(
    Recuerdo("España", R.drawable.imanespana),
    Recuerdo("Francia", R.drawable.imanfrancia),
    Recuerdo("Italia", R.drawable.imanitalia),

)

data class FotoRecuerdo(
    val uri: String,
    val timestamp: Long
)