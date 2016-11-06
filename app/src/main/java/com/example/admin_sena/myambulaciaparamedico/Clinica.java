package com.example.admin_sena.myambulaciaparamedico;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;


public class Clinica {
    private String direccion;
    private String nombre;
    private Location ubicacion;
    private String IdAmbulancia;
    private String IdPaciente;
    public Clinica(String nombre, String direccion, LatLng ubicacion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ubicacion = new Location("");
        this.ubicacion.setLatitude(ubicacion.latitude);
        this.ubicacion.setLongitude(ubicacion.longitude);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Location getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Location ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getIdAmbulancia() {
        return IdAmbulancia;
    }

    public void setIdAmbulancia(String idAmbulancia) {
        IdAmbulancia = idAmbulancia;
    }

    public String getIdPaciente() {
        return IdPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        IdPaciente = idPaciente;
    }
}
