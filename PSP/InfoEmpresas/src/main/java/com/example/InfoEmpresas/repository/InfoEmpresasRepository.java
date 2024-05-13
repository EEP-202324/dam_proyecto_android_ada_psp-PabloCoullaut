package com.example.InfoEmpresas.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.InfoEmpresas.model.InfoEmpresas;

public interface InfoEmpresasRepository extends JpaRepository<InfoEmpresas, Long> {
	
	Optional<InfoEmpresas> findByNombre(String nombre);
	
	Page<InfoEmpresas> findByNombre(String nombre, Pageable pageable);
}
