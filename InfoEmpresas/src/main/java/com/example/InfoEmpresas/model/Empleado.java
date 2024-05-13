package com.example.InfoEmpresas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String cargo;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private InfoEmpresas empresa;

    public Empleado() {
    }

    public Empleado(String nombre, String cargo, InfoEmpresas empresa) {
        this.nombre = nombre;
        this.cargo = cargo;
        this.empresa = empresa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public InfoEmpresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(InfoEmpresas empresa) {
        this.empresa = empresa;
    }
}
