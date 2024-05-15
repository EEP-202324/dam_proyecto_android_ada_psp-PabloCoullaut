package com.example.InfoEmpresas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.InfoEmpresas.model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
	
	List<Empleado> findByEmpresaId(Long empresaId);
}
