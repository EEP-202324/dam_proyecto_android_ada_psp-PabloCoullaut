package com.example.InfoEmpresas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoEmpresasRepository extends JpaRepository<InfoEmpresas, Long> {
	
	Optional<InfoEmpresas> findByNombre(String nombre);
}
