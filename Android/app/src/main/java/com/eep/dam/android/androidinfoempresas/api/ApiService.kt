package com.eep.dam.android.androidinfoempresas.api

import com.eep.dam.android.androidinfoempresas.model.Empleado
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("infoempresas")
    suspend fun getAllEmpresas(): List<InfoEmpresas>

    @POST("infoempresas")
    suspend fun createEmpresa(@Body empresa: InfoEmpresas): InfoEmpresas

    @PUT("infoempresas/{id}")
    suspend fun updateEmpresa(@Path("id") id: Long, @Body empresa: InfoEmpresas): InfoEmpresas

    @DELETE("infoempresas/{id}")
    suspend fun deleteEmpresa(@Path("id") id: Long): Response<Unit>

    @GET("empleados/empresa/{empresaId}")
    suspend fun getEmpleadosByEmpresaId(@Path("empresaId") empresaId: Long): List<Empleado>

    @POST("empleados")
    suspend fun createEmpleado(@Body empleado: Empleado, @Query("nombreEmpresa") nombreEmpresa: String): Empleado

    @PUT("empleados/{id}")
    suspend fun updateEmpleado(@Path("id") id: Long, @Body empleado: Empleado): Empleado

    @DELETE("empleados/{id}")
    suspend fun deleteEmpleado(@Path("id") id: Long): Response<Unit>
}
