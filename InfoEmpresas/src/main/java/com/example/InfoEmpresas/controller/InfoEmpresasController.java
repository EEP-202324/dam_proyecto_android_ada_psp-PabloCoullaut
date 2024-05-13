package com.example.InfoEmpresas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.example.InfoEmpresas.model.InfoEmpresas;
import com.example.InfoEmpresas.repository.InfoEmpresasRepository;

@RestController
@RequestMapping("/infoempresas")
public class InfoEmpresasController {

    @Autowired
    private InfoEmpresasRepository infoEmpresasRepository;

    @PostMapping
    public InfoEmpresas crearInfoEmpresas(@RequestBody InfoEmpresas infoEmpresas) {
        return infoEmpresasRepository.save(infoEmpresas);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllInfoEmpresas(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String nombre) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort[0]).descending());

            Page<InfoEmpresas> infoEmpresasPage;
            if (nombre != null) {
                infoEmpresasPage = infoEmpresasRepository.findByNombre(nombre, pageable);
            } else {
                infoEmpresasPage = infoEmpresasRepository.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("infoEmpresas", infoEmpresasPage.getContent());
            response.put("currentPage", infoEmpresasPage.getNumber());
            response.put("totalItems", infoEmpresasPage.getTotalElements());
            response.put("totalPages", infoEmpresasPage.getTotalPages());

            return ResponseEntity.ok(response);
        } else {
            List<InfoEmpresas> allInfoEmpresas = infoEmpresasRepository.findAll();
            return ResponseEntity.ok(allInfoEmpresas);
        }
    }
    
    @GetMapping
    public List<InfoEmpresas> getAllInfoEmpresas() { 
    	return infoEmpresasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InfoEmpresas> findInfoEmpresasById(@PathVariable Long id) {
        Optional<InfoEmpresas> empresaOptional = infoEmpresasRepository.findById(id);
        if (empresaOptional.isPresent()) {
            return ResponseEntity.ok(empresaOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<InfoEmpresas> findInfoEmpresasByNombre(@PathVariable String nombre) {
        Optional<InfoEmpresas> empresaOptional = infoEmpresasRepository.findByNombre(nombre);
        if (empresaOptional.isPresent()) {
            return ResponseEntity.ok(empresaOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InfoEmpresas> actualizarInfoEmpresas(
            @PathVariable Long id, @RequestBody InfoEmpresas nuevaInfoEmpresas) {
        Optional<InfoEmpresas> empresaOptional = infoEmpresasRepository.findById(id);
        if (empresaOptional.isPresent()) {
            InfoEmpresas empresaExistente = empresaOptional.get();
        
            empresaExistente.setNombre(nuevaInfoEmpresas.getNombre());
            empresaExistente.setDescripcion(nuevaInfoEmpresas.getDescripcion());
            empresaExistente.setDireccion(nuevaInfoEmpresas.getDireccion());
            
            InfoEmpresas empresaActualizada = infoEmpresasRepository.save(empresaExistente);
            return ResponseEntity.ok(empresaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<InfoEmpresas> actualizarInfoEmpresas(
            @PathVariable String nombre, @RequestBody InfoEmpresas nuevaInfoEmpresas) {
        Optional<InfoEmpresas> empresaOptional = infoEmpresasRepository.findByNombre(nombre);
        if (empresaOptional.isPresent()) {
            InfoEmpresas empresaExistente = empresaOptional.get();
        
            empresaExistente.setNombre(nuevaInfoEmpresas.getNombre());
            empresaExistente.setDescripcion(nuevaInfoEmpresas.getDescripcion());
            empresaExistente.setDireccion(nuevaInfoEmpresas.getDireccion());
            
            InfoEmpresas empresaActualizada = infoEmpresasRepository.save(empresaExistente);
            return ResponseEntity.ok(empresaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInfoEmpresas(@PathVariable Long id) {
        Optional<InfoEmpresas> empresaOptional = infoEmpresasRepository.findById(id);
        if (empresaOptional.isPresent()) {
            infoEmpresasRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
