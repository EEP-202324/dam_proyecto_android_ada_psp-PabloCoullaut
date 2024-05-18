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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eep.dam.android.androidinfoempresas.api.RetrofitClient
import com.eep.dam.android.androidinfoempresas.model.InfoEmpresas
import com.eep.dam.android.androidinfoempresas.viewmodel.EmpresaViewModel
import com.eep.dam.android.androidinfoempresas.model.Empleado
import com.eep.dam.android.androidinfoempresas.ui.theme.AndroidInfoEmpresasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidInfoEmpresasTheme {
                val navController = rememberNavController()
                val apiService = RetrofitClient.apiRepository
                val viewModel: EmpresaViewModel = viewModel()
                viewModel.apiService = apiService

                LaunchedEffect(Unit) {
                    viewModel.getAllEmpresas()
                }

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        val empresas by viewModel.empresas.observeAsState(emptyList())
                        MainScreen(
                            empresas = empresas,
                            onAddEmpresa = { empresa ->
                                viewModel.createEmpresa(empresa)
                            },
                            onEmpresaClick = { empresa ->
                                navController.navigate("details/${empresa.id}")
                            },
                            onDeleteEmpresa = { empresa ->
                                viewModel.deleteEmpresa(empresa.id!!)
                            },
                            onUpdateEmpresa = { empresa ->
                                viewModel.updateEmpresa(empresa.id!!, empresa)
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
                            EmpresaDetailScreen(
                                navController = navController,
                                empresa = it,
                                empleados = empleados,
                                viewModel = viewModel
                            )
                        }
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
    onEmpresaClick: (InfoEmpresas) -> Unit,
    onDeleteEmpresa: (InfoEmpresas) -> Unit,
    onUpdateEmpresa: (InfoEmpresas) -> Unit
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

            EmpresaList(
                empresas = filteredEmpresas,
                onEmpresaClick = onEmpresaClick,
                onDeleteEmpresa = onDeleteEmpresa,
                onUpdateEmpresa = onUpdateEmpresa
            )
        }
    }
}

@Composable
fun EmpresaList(
    empresas: List<InfoEmpresas>,
    onEmpresaClick: (InfoEmpresas) -> Unit,
    onDeleteEmpresa: (InfoEmpresas) -> Unit,
    onUpdateEmpresa: (InfoEmpresas) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(empresas) { empresa ->
            EmpresaItem(empresa, onEmpresaClick, onDeleteEmpresa, onUpdateEmpresa)
            Divider()
        }
    }
}

@Composable
fun EmpresaItem(
    empresa: InfoEmpresas,
    onEmpresaClick: (InfoEmpresas) -> Unit,
    onDeleteEmpresa: (InfoEmpresas) -> Unit,
    onUpdateEmpresa: (InfoEmpresas) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf(empresa.nombre) }
    var descripcion by remember { mutableStateOf(empresa.descripcion) }
    var direccion by remember { mutableStateOf(empresa.direccion) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEmpresaClick(empresa) },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Empresa: ${empresa.nombre}", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Descripción: ${empresa.descripcion}", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Dirección: ${empresa.direccion}", style = MaterialTheme.typography.body1)
            }
            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Empresa")
                }
                IconButton(onClick = { onDeleteEmpresa(empresa) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Empresa")
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "Editar Empresa") },
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
                        val updatedEmpresa = empresa.copy(
                            nombre = nombre,
                            descripcion = descripcion,
                            direccion = direccion
                        )
                        onUpdateEmpresa(updatedEmpresa)
                        showEditDialog = false
                    }
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EmpresaDetailScreen(
    navController: NavController,
    empresa: InfoEmpresas,
    empleados: List<Empleado>,
    viewModel: EmpresaViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var selectedEmpleado by remember { mutableStateOf<Empleado?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalles de la Empresa") },
                backgroundColor = Color(0xFF0288D1),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteEmpresa(empresa.id!!)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Empresa")
                    }
                }
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
                EmpleadoItem(empleado, onEdit = {
                    selectedEmpleado = empleado
                    nombre = empleado.nombre
                    cargo = empleado.cargo
                    showEditDialog = true
                }) {
                    viewModel.deleteEmpleado(empleado.id, empresa.id!!)
                }
                Divider()
            }
        }

        if (showDialog) {
            EmpleadoDialog(
                title = "Añadir Empleado",
                nombre = nombre,
                onNombreChange = { nombre = it },
                cargo = cargo,
                onCargoChange = { cargo = it },
                onConfirm = {
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
                },
                onDismiss = { showDialog = false }
            )
        }

        if (showEditDialog) {
            selectedEmpleado?.let { empleado ->
                EmpleadoDialog(
                    title = "Editar Empleado",
                    nombre = nombre,
                    onNombreChange = { nombre = it },
                    cargo = cargo,
                    onCargoChange = { cargo = it },
                    onConfirm = {
                        val updatedEmpleado = empleado.copy(
                            nombre = nombre,
                            cargo = cargo
                        )
                        viewModel.updateEmpleado(updatedEmpleado.id, updatedEmpleado)
                        showEditDialog = false
                    },
                    onDismiss = { showEditDialog = false }
                )
            }
        }
    }
}

@Composable
fun EmpleadoItem(empleado: Empleado, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Nombre: ${empleado.nombre}", style = MaterialTheme.typography.body1)
            Text(text = "Cargo: ${empleado.cargo}", style = MaterialTheme.typography.body2)
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Empleado")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Empleado")
            }
        }
    }
}

@Composable
fun EmpleadoDialog(
    title: String,
    nombre: String,
    onNombreChange: (String) -> Unit,
    cargo: String,
    onCargoChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cargos = listOf("Gerente", "Desarrollador", "Diseñador", "QA", "Soporte")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = cargo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cargo") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        cargos.forEach { cargoItem ->
                            DropdownMenuItem(onClick = {
                                onCargoChange(cargoItem)
                                expanded = false
                            }) {
                                Text(text = cargoItem)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
