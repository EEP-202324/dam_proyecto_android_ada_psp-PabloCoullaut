package com.eep.dam.android.androidinfoempresas.api

import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @GET("/infoempresas")
    suspend fun getAllEmpresas(): List<InfoEmpresas>

    @POST("/infoempresas")
    suspend fun createEmpresa(@Body empresa: InfoEmpresas): InfoEmpresas
}