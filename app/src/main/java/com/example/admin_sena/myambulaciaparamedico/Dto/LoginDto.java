package com.example.admin_sena.myambulaciaparamedico.Dto;

/**
 * Created by oscar on 22/03/16.
 */
public class LoginDto {
    String Cedula;
    String Contraseña;

    public String getCedula() {
        return Cedula;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }
}
