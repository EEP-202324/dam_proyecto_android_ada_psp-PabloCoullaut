package com.eep.dam.android.androidinfoempresas.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eep.dam.android.androidinfoempresas.api.RetrofitClient
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import com.eep.dam.android.androidinfoempresas.viewmodel.EmpresaViewModel
import com.eep.dam.android.androidinfoempresas.model.Empleado

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val apiService = RetrofitClient.apiRepository
            val viewModel: EmpresaViewModel = viewModel()
            viewModel.apiService = apiService

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "LaunchedEffect: Calling getAllEmpresas")
                viewModel.getAllEmpresas()
            }

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    val empresas by viewModel.empresas.observeAsState(emptyList())
                    Log.d("MainActivity", "Observed empresas: $empresas")
                    MainScreen(
                        empresas = empresas,
                        onAddEmpresa = { empresa ->
                            Log.d("MainActivity", "onAddEmpresa: $empresa")
                            viewModel.createEmpresa(empresa)
                        },
                        onEmpresaClick = { empresa ->
                            navController.navigate("details/${empresa.id}")
                        }
                    )
                }
                composable("details/{empresaId}") { backStackEntry ->
                    val empresaId = backStackEntry.arguments?.getString("empresaId")?.toLong()
                    val empresa = viewModel.empresas.value?.find { it.id == empresaId }
                    empresa?.let {
                        LaunchedEffect(empresaId) {
                            viewModel.getEmpleadosByEmpresaId(empresaId!!)
                        }
                        val empleados by viewModel.empleados.observeAsState(emptyList())
                        EmpresaDetailScreen(empresa = it, empleados = empleados, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    empresas: List<InfoEmpresas>,
    onAddEmpresa: (InfoEmpresas) -> Unit,
    onEmpresaClick: (InfoEmpresas) -> Unit
) {
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

            EmpresaList(empresas = filteredEmpresas, onEmpresaClick = onEmpresaClick)
        }
    }
}

@Composable
fun EmpresaList(empresas: List<InfoEmpresas>, onEmpresaClick: (InfoEmpresas) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(empresas) { empresa ->
            EmpresaItem(empresa, onEmpresaClick)
            Divider()
        }
    }
}

@Composable
fun EmpresaItem(empresa: InfoEmpresas, onEmpresaClick: (InfoEmpresas) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEmpresaClick(empresa) },
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

@Composable
fun EmpresaDetailScreen(
    empresa: InfoEmpresas,
    empleados: List<Empleado>,
    viewModel: EmpresaViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalles de la Empresa") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text(text = "Empresa: ${empresa.nombre}", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Descripción: ${empresa.descripcion}", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Dirección: ${empresa.direccion}", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Empleados", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(8.dp))
            empleados.forEach { empleado ->
                EmpleadoItem(empleado)
                Divider()
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Añadir Empleado") },
                text = {
                    Column {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") }
                        )
                        TextField(
                            value = cargo,
                            onValueChange = { cargo = it },
                            label = { Text("Cargo") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val nuevoEmpleado = Empleado(
                                id = 0,
                                nombre = nombre,
                                cargo = cargo,
                                empresa = empresa
                            )
                            viewModel.createEmpleado(nuevoEmpleado, empresa.nombre)
                            nombre = ""
                            cargo = ""
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
    }
}

@Composable
fun EmpleadoItem(empleado: Empleado) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Nombre: ${empleado.nombre}", style = MaterialTheme.typography.body1)
        Text(text = "Cargo: ${empleado.cargo}", style = MaterialTheme.typography.body2)
    }
}
