package com.eep.dam.android.androidinfoempresas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eep.dam.android.androidinfoempresas.api.ApiService
import com.eep.dam.android.androidinfoempresas.model.Empleado
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

    private val _empleados = MutableLiveData<List<Empleado>>()
    val empleados: LiveData<List<Empleado>> get() = _empleados

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

    fun updateEmpresa(id: Long, empresa: InfoEmpresas) {
        viewModelScope.launch {
            try {
                val updatedEmpresa = withContext(Dispatchers.IO) {
                    apiService.updateEmpresa(id, empresa)
                }
                Log.d("EmpresaViewModel", "Empresa updated successfully: $updatedEmpresa")
                getAllEmpresas()
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error updating empresa", e)
            }
        }
    }

    fun getEmpleadosByEmpresaId(empresaId: Long) {
        viewModelScope.launch {
            try {
                val empleadosList = withContext(Dispatchers.IO) {
                    apiService.getEmpleadosByEmpresaId(empresaId)
                }
                _empleados.postValue(empleadosList)
                Log.d("EmpresaViewModel", "Empleados fetched successfully: $empleadosList")
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error fetching empleados", e)
            }
        }
    }

    fun createEmpleado(empleado: Empleado, nombreEmpresa: String) {
        viewModelScope.launch {
            try {
                Log.d("EmpresaViewModel", "Creating empleado: $empleado with empresa: $nombreEmpresa")
                val newEmpleado = withContext(Dispatchers.IO) {
                    apiService.createEmpleado(empleado, nombreEmpresa)
                }
                Log.d("EmpresaViewModel", "Empleado created successfully: $newEmpleado")
                getEmpleadosByEmpresaId(newEmpleado.empresa.id ?: 0)
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error creating empleado", e)
            }
        }
    }

    fun updateEmpleado(id: Long, empleado: Empleado) {
        viewModelScope.launch {
            try {
                val updatedEmpleado = withContext(Dispatchers.IO) {
                    apiService.updateEmpleado(id, empleado)
                }
                Log.d("EmpresaViewModel", "Empleado updated successfully: $updatedEmpleado")
                getEmpleadosByEmpresaId(empleado.empresa.id ?: 0)
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error updating empleado", e)
            }
        }
    }

    fun deleteEmpresa(id: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    apiService.deleteEmpresa(id)
                }
                Log.d("EmpresaViewModel", "Empresa deleted successfully")
                getAllEmpresas()
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error deleting empresa", e)
            }
        }
    }

    fun deleteEmpleado(id: Long, empresaId: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    apiService.deleteEmpleado(id)
                }
                Log.d("EmpresaViewModel", "Empleado deleted successfully")
                getEmpleadosByEmpresaId(empresaId)
            } catch (e: Exception) {
                Log.e("EmpresaViewModel", "Error deleting empleado", e)
            }
        }
    }
}
