package com.eep.dam.android.androidinfoempresas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eep.dam.android.androidinfoempresas.api.ApiService
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmpresaViewModel : ViewModel() {

    lateinit var apiService: ApiService

    private val _empresas = MutableLiveData<List<InfoEmpresas>>()
    val empresas: LiveData<List<InfoEmpresas>> get() = _empresas

    init {
        Log.d("EmpresaViewModel", "ViewModel initialized")
    }

    fun getAllEmpresas() {
        viewModelScope.launch {
            try {
                val empresasList = withContext(Dispatchers.IO) {
                    apiService.getAllEmpresas()
                }
                _empresas.postValue(empresasList)
                Log.d("EmpresaViewModel", "Empresas fetched successfully: $empresasList")
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error fetching empresas", e)
            }
        }
    }

    fun createEmpresa(empresa: InfoEmpresas) {
        viewModelScope.launch {
            try {
                val newEmpresa = withContext(Dispatchers.IO) {
                    apiService.createEmpresa(empresa)
                }
                Log.d("EmpresaViewModel", "Empresa created successfully: $newEmpresa")
                getAllEmpresas()
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error creating empresa", e)
            }
        }
    }
}
