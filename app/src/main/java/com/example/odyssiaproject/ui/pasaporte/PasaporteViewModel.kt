package com.example.odyssiaproject.ui.pasaporte

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.odyssiaproject.data.cargarEstadoNevera
import com.example.odyssiaproject.data.guardarEstadoNevera
import com.example.odyssiaproject.model.FotoRecuerdo
import com.example.odyssiaproject.model.Recuerdo
import com.example.odyssiaproject.model.recuerdosDisponibles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PasaporteViewModel(application: Application) : AndroidViewModel(application) {

    private val _imanesEnNevera = MutableLiveData<List<Recuerdo>>(emptyList())
    val imanesEnNevera: LiveData<List<Recuerdo>> = _imanesEnNevera

    private val _fotosEnNevera = MutableLiveData<List<FotoRecuerdo>>(emptyList())
    val fotosEnNevera: LiveData<List<FotoRecuerdo>> = _fotosEnNevera

    private val _albumDeFotos = MutableLiveData<List<FotoRecuerdo>>(emptyList())
    val albumDeFotos: LiveData<List<FotoRecuerdo>> = _albumDeFotos

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isDeleteMode = MutableLiveData<Boolean>(false)
    val isDeleteMode: LiveData<Boolean> = _isDeleteMode

    private val _isAlbumDeleteMode = MutableLiveData<Boolean>(false)
    val isAlbumDeleteMode: LiveData<Boolean> = _isAlbumDeleteMode

    private val _itemAmpliado = MutableLiveData<Any?>(null)
    val itemAmpliado: LiveData<Any?> = _itemAmpliado

    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()

    val listaDeRecuerdosDisponibles = recuerdosDisponibles

    fun cargarEstadoInicial() {
        if ((_imanesEnNevera.value?.isNotEmpty() == true || _fotosEnNevera.value?.isNotEmpty() == true) || _isLoading.value == true) return
        viewModelScope.launch {
            _isLoading.value = true
            val (nombresImanes, fotosNeveraStr, albumFotosStr) = withContext(viewModelScope.coroutineContext) {
                cargarEstadoNevera(getApplication())
            }
            val imanesCargados = recuerdosDisponibles.filter { nombresImanes.contains(it.nombrePais) }
            fun parsearFotos(strings: Set<String>): List<FotoRecuerdo> {
                return strings.mapNotNull { fotoString ->
                    val partes = fotoString.split('|')
                    if (partes.size == 2) FotoRecuerdo(uri = partes[0], timestamp = partes[1].toLong()) else null
                }
            }
            _imanesEnNevera.value = imanesCargados
            _fotosEnNevera.value = parsearFotos(fotosNeveraStr)
            _albumDeFotos.value = parsearFotos(albumFotosStr)
            _isLoading.value = false
        }
    }

    private fun guardarEstadoActual() {
        viewModelScope.launch(Dispatchers.IO) {
            val imanes = _imanesEnNevera.value ?: emptyList()
            val fotosNevera = _fotosEnNevera.value ?: emptyList()
            val album = _albumDeFotos.value ?: emptyList()
            guardarEstadoNevera(getApplication(), imanes, fotosNevera, album)
        }
    }

    fun añadirIman(recuerdo: Recuerdo) {
        val currentList = _imanesEnNevera.value ?: emptyList()
        if (currentList.size >= 15) {
            viewModelScope.launch { _eventChannel.send("El congelador está lleno") }
            return
        }
        if (!currentList.any { it.idImagen == recuerdo.idImagen }) {
            _imanesEnNevera.value = currentList + recuerdo
            guardarEstadoActual()
        }
    }

    fun eliminarIman(recuerdo: Recuerdo) {
        val currentList = _imanesEnNevera.value ?: emptyList()
        _imanesEnNevera.value = currentList.filterNot { it.idImagen == recuerdo.idImagen }
        guardarEstadoActual()
    }

    fun añadirFotoAlAlbum(uri: Uri) {
        val foto = FotoRecuerdo(uri.toString(), System.currentTimeMillis())
        val currentAlbum = _albumDeFotos.value ?: emptyList()
        if (!currentAlbum.any { it.uri == foto.uri }) {
            _albumDeFotos.value = currentAlbum + foto
            guardarEstadoActual()
        }
        toggleAlbumDeleteMode(forceOff = true)
    }

    fun colocarFotoEnNevera(foto: FotoRecuerdo) {
        val currentList = _fotosEnNevera.value ?: emptyList()
        if (currentList.size >= 9) {
            viewModelScope.launch { _eventChannel.send("Ya no caben más fotos en la nevera") }
            return
        }
        if (!currentList.any { it.uri == foto.uri }) {
            _fotosEnNevera.value = currentList + foto
            guardarEstadoActual()
        }
    }

    fun eliminarFotoDeLaNevera(foto: FotoRecuerdo) {
        val currentList = _fotosEnNevera.value ?: emptyList()
        _fotosEnNevera.value = currentList.filterNot { it.uri == foto.uri }
        guardarEstadoActual()
    }

    fun eliminarFotoDelAlbum(foto: FotoRecuerdo) {
        val currentAlbum = _albumDeFotos.value ?: emptyList()
        val updatedAlbum = currentAlbum.filterNot { it.uri == foto.uri }
        _albumDeFotos.value = updatedAlbum
        eliminarFotoDeLaNevera(foto)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileUri = Uri.parse(foto.uri)
                if (fileUri.scheme == "file" && fileUri.path != null) {
                    File(fileUri.path!!).takeIf { it.exists() }?.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (updatedAlbum.isEmpty()) {
            toggleAlbumDeleteMode(forceOff = true)
        }
    }

    fun ampliarItem(item: Any) {
        _itemAmpliado.value = item
    }

    fun cerrarVistaAmpliada() {
        _itemAmpliado.value = null
    }

    fun toggleDeleteMode() {
        _isDeleteMode.value = !(_isDeleteMode.value ?: false)
    }

    fun toggleAlbumDeleteMode(forceOff: Boolean = false) {
        if (forceOff) {
            _isAlbumDeleteMode.value = false
        } else {
            if ((_albumDeFotos.value ?: emptyList()).isNotEmpty()) {
                _isAlbumDeleteMode.value = !(_isAlbumDeleteMode.value ?: false)
            }
        }
    }
}