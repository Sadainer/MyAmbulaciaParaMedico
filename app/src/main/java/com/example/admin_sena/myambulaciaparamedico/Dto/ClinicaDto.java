package com.example.admin_sena.myambulaciaparamedico.Dto;


public class ClinicaDto {

    public String idAmbulancia;
    public String idPaciente;
    public double longitude, latitude;

    public ClinicaDto(String idAmbulancia, String idPaciente, double longitude, double latitude) {
        this.idAmbulancia = idAmbulancia;
        this.idPaciente = idPaciente;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
