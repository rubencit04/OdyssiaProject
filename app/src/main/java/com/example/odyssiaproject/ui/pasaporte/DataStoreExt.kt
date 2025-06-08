package com.example.odyssiaproject.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.odyssiaproject.model.FotoRecuerdo
import com.example.odyssiaproject.model.Recuerdo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "estado_nevera")
private val IMANES_EN_NEVERA = stringSetPreferencesKey("imanes_en_nevera")
private val FOTOS_EN_NEVERA = stringSetPreferencesKey("fotos_en_nevera")
private val ALBUM_DE_FOTOS = stringSetPreferencesKey("album_de_fotos")

/**
 * Guarda el estado completo.
 */
suspend fun guardarEstadoNevera(contexto: Context, imanes: List<Recuerdo>, fotosEnNevera: List<FotoRecuerdo>, albumDeFotos: List<FotoRecuerdo>) {
    val nombresDeImanes = imanes.map { it.nombrePais }.toSet()
    val urisFotosEnNevera = fotosEnNevera.map { "${it.uri}|${it.timestamp}" }.toSet()
    val urisAlbumDeFotos = albumDeFotos.map { "${it.uri}|${it.timestamp}" }.toSet()

    contexto.dataStore.edit { prefs ->
        prefs[IMANES_EN_NEVERA] = nombresDeImanes
        prefs[FOTOS_EN_NEVERA] = urisFotosEnNevera
        prefs[ALBUM_DE_FOTOS] = urisAlbumDeFotos
    }
}

/**
 * Carga el estado completo. Devuelve un Triplete.
 */
suspend fun cargarEstadoNevera(contexto: Context): Triple<Set<String>, Set<String>, Set<String>> {
    return contexto.dataStore.data
        .map { preferences ->
            val imanes = preferences[IMANES_EN_NEVERA] ?: emptySet()
            val fotosEnNevera = preferences[FOTOS_EN_NEVERA] ?: emptySet()
            val albumDeFotos = preferences[ALBUM_DE_FOTOS] ?: emptySet()
            Triple(imanes, fotosEnNevera, albumDeFotos)
        }.first()
}
