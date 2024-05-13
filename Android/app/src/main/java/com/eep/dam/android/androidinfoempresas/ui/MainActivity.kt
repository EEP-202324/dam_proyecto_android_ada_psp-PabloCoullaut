package com.eep.dam.android.androidinfoempresas.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import com.eep.dam.android.androidinfoempresas.viewmodel.EmpresasViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen()
        }
    }
}

@Composable
fun Screen() {
    val empresasViewModel: EmpresasViewModel = viewModel()

    val empresas by empresasViewModel.empresas.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = true) {
        empresasViewModel.fetchEmpresas()
    }

    EmpresaList(empresas = empresas)
}

@Composable
fun EmpresaList(empresas: List<InfoEmpresas>) {
    LazyColumn {
        items(empresas) { empresa ->
            EmpresaListItem(empresa = empresa)
            Divider(color = Color.Gray)
        }
    }
}

@Composable
fun EmpresaListItem(empresa: InfoEmpresas) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = empresa.nombre, fontSize = 18.sp)
        Text(text = empresa.descripcion, fontSize = 14.sp)
        Text(text = empresa.direccion, fontSize = 14.sp)
    }
}

@Composable
fun AddEmpresa(onAddEmpresa: (InfoEmpresas) -> Unit) {
    val nombreState = remember { mutableStateOf("") }
    val descripcionState = remember { mutableStateOf("") }
    val direccionState = remember { mutableStateOf("") }

    Column {
        TextField(
            value = nombreState.value,
            onValueChange = { nombreState.value = it },
            label = { Text("Nombre de la empresa") }
        )
        TextField(
            value = descripcionState.value,
            onValueChange = { descripcionState.value = it },
            label = { Text("Descripción de la empresa") }
        )
        TextField(
            value = direccionState.value,
            onValueChange = { direccionState.value = it },
            label = { Text("Dirección de la empresa") }
        )
        Button(onClick = {
            val nuevaEmpresa = InfoEmpresas(
                nombre = nombreState.value,
                descripcion = descripcionState.value,
                direccion = direccionState.value
            )
            onAddEmpresa(nuevaEmpresa)
            nombreState.value = ""
            descripcionState.value = ""
            direccionState.value = ""
        }) {
            Text("Añadir Empresa")
        }
    }
}
