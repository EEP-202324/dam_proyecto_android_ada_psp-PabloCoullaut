package com.eep.dam.android.androidinfoempresas.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eep.dam.android.androidinfoempresas.api.RetrofitClient
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import com.eep.dam.android.androidinfoempresas.viewmodel.EmpresaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val apiService = RetrofitClient.apiRepository
            val viewModel: EmpresaViewModel = viewModel()
            viewModel.apiService = apiService

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "LaunchedEffect: Calling getAllEmpresas")
                viewModel.getAllEmpresas()
            }

            val empresas by viewModel.empresas.observeAsState(emptyList())
            Log.d("MainActivity", "Observed empresas: $empresas")
            MainScreen(empresas = empresas, onAddEmpresa = { empresa ->
                Log.d("MainActivity", "onAddEmpresa: $empresa")
                viewModel.createEmpresa(empresa)
            })
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(empresas: List<InfoEmpresas>, onAddEmpresa: (InfoEmpresas) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Añadir Empresa") },
            text = {
                Column {
                    TextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") }
                    )
                    TextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") }
                    )
                    TextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d("MainScreen", "Adding empresa: $nombre, $descripcion, $direccion")
                        onAddEmpresa(
                            InfoEmpresas(
                                nombre = nombre,
                                descripcion = descripcion,
                                direccion = direccion
                            )
                        )
                        nombre = ""
                        descripcion = ""
                        direccion = ""
                        showDialog = false
                    }
                ) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Empresa") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            val filteredEmpresas = empresas.filter { empresa ->
                empresa.nombre.contains(searchQuery, ignoreCase = true)
            }

            EmpresaList(empresas = filteredEmpresas)
        }
    }
}

@Composable
fun EmpresaList(empresas: List<InfoEmpresas>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(empresas) { empresa ->
            EmpresaItem(empresa)
            Divider()
        }
    }
}

@Composable
fun EmpresaItem(empresa: InfoEmpresas) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Empresa: ${empresa.nombre}", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descripción: ${empresa.descripcion}", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dirección: ${empresa.direccion}", style = MaterialTheme.typography.body1)
        }
    }
}
