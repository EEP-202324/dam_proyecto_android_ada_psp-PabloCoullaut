package com.example.InfoEmpresas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.InfoEmpresas.model.Empleado;
import com.example.InfoEmpresas.model.InfoEmpresas;
import com.example.InfoEmpresas.repository.EmpleadoRepository;
import com.example.InfoEmpresas.repository.InfoEmpresasRepository;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private InfoEmpresasRepository infoEmpresasRepository;

    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Empleado>> getEmpleadosByEmpresaId(@PathVariable Long empresaId) {
        List<Empleado> empleados = empleadoRepository.findByEmpresaId(empresaId);
        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Empleado> createEmpleado(@RequestBody Empleado empleado, @RequestParam String nombreEmpresa) {
        Optional<InfoEmpresas> empresaOptional = infoEmpresasRepository.findByNombre(nombreEmpresa);
        if (empresaOptional.isPresent()) {
            empleado.setEmpresa(empresaOptional.get());
            Empleado savedEmpleado = empleadoRepository.save(empleado);
            return new ResponseEntity<>(savedEmpleado, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable Long id) {
        Optional<Empleado> empleadoOptional = empleadoRepository.findById(id);
        if (empleadoOptional.isPresent()) {
            return ResponseEntity.ok(empleadoOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> updateEmpleado(@PathVariable Long id, @RequestBody Empleado empleado) {
        Optional<Empleado> empleadoOptional = empleadoRepository.findById(id);
        if (empleadoOptional.isPresent()) {
            Empleado existingEmpleado = empleadoOptional.get();
            existingEmpleado.setNombre(empleado.getNombre());
            existingEmpleado.setCargo(empleado.getCargo());
            existingEmpleado.setEmpresa(empleado.getEmpresa());
            return ResponseEntity.ok(empleadoRepository.save(existingEmpleado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpleado(@PathVariable Long id) {
        Optional<Empleado> empleadoOptional = empleadoRepository.findById(id);
        if (empleadoOptional.isPresent()) {
            empleadoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}