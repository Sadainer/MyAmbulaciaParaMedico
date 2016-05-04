package com.example.admin_sena.myambulaciaparamedico.Dto;

import java.io.Serializable;

/**
 * Created by oscar on 3/05/16.
 */
public class UbicacionPacienteDto implements Serializable{
    private int IdPaciente;
    private int NumeroPacientes;
    private String Tipoemergencia;
    private String Direccion;
    private Double Latitud;
    private Double Longitud;

    public int getIdPaciente() {
        return IdPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        IdPaciente = idPaciente;
    }

    public int getNumeroPacientes() {
        return NumeroPacientes;
    }

    public void setNumeroPacientes(int numeroPacientes) {
        NumeroPacientes = numeroPacientes;
    }

    public String getTipoemergencia() {
        return Tipoemergencia;
    }

    public void setTipoemergencia(String tipoemergencia) {
        Tipoemergencia = tipoemergencia;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }
}
