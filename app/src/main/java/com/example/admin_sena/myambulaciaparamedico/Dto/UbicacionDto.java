package com.example.admin_sena.myambulaciaparamedico.Dto;

import java.util.Date;

/**
 * Created by Admin_Sena on 01/03/2016.
 */
public class UbicacionDto {
    private int UbicacionAmbulancia;
    private String Cedula;
    private Date Fecha;
    private Double Latitud;
    private Double Longitud;

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        Fecha = fecha;
    }

    public String getIdAmbulancia() {
        return Cedula;
    }

    public void setIdAmbulancia(String idAmbulancia) {
        Cedula = idAmbulancia;
    }

    public int getUbicacionAmbulancia() {
        return UbicacionAmbulancia;
    }

    public void setUbicacionAmbulancia(int ubicacionAmbulancia) {
        UbicacionAmbulancia = ubicacionAmbulancia;
    }


}
