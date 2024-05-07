package com.example.InfoEmpresas;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/infoempresas")
public class InfoEmpresasController {

    @Autowired
    private InfoEmpresasRepository infoEmpresasRepository;

    @PostMapping
    public InfoEmpresas crearInfoEmpresas(@RequestBody InfoEmpresas infoEmpresas) {
        return infoEmpresasRepository.save(infoEmpresas);
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
}
