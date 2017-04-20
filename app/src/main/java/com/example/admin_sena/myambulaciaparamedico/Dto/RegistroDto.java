package com.example.admin_sena.myambulaciaparamedico.Dto;

/**
 * Created by oscar on 22/03/16.
 */
public class RegistroDto {
    private String Nombres;
    private String Apellidos;
    private String Cedula;
    private String Correo;
    private String Password;

    public String getNombres() {
        return Nombres;
    }

    public String getCedula() {
        return Cedula;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }
}
