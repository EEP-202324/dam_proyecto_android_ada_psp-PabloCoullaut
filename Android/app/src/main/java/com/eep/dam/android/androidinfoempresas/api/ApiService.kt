package com.eep.dam.android.androidinfoempresas.api

import com.eep.dam.android.androidinfoempresas.model.Empleado
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("infoempresas")
    suspend fun getAllEmpresas(): List<InfoEmpresas>

    @POST("infoempresas")
    suspend fun createEmpresa(@Body empresa: InfoEmpresas): InfoEmpresas

    @GET("empleados/empresa/{empresaId}")
    suspend fun getEmpleadosByEmpresaId(@Path("empresaId") empresaId: Long): List<Empleado>

    @POST("empleados")
    suspend fun createEmpleado(@Body empleado: Empleado, @Query("nombreEmpresa") nombreEmpresa: String): Empleado
}
