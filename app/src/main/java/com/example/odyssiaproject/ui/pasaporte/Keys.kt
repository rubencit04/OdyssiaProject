package com.example.odyssiaproject.data

import androidx.datastore.preferences.core.intPreferencesKey

object PreferenceKeys {
    fun keyX(nombrePais: String) = intPreferencesKey("pos_x_$nombrePais")
    fun keyY(nombrePais: String) = intPreferencesKey("pos_y_$nombrePais")
}