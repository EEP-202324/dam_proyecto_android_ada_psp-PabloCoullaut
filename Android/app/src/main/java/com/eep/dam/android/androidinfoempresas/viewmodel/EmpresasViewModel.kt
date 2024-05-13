package com.eep.dam.android.androidinfoempresas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eep.dam.android.androidinfoempresas.api.RetrofitClient
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import kotlinx.coroutines.launch

class EmpresasViewModel : ViewModel() {
    private val _empresas = MutableLiveData<List<InfoEmpresas>>()
    val empresas: LiveData<List<InfoEmpresas>> = _empresas

    fun fetchEmpresas() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiRepository.getAllEmpresas()
                _empresas.value = response
            } catch (e: Exception) {
            }
        }
    }
}