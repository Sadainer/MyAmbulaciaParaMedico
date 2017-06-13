package com.example.admin_sena.myambulaciaparamedico.Dto;

public class LoginDto {
    String Cedula;
    String Password;

    public String getCedula() {
        return Cedula;
    }

    public String getPassword() {
        return Password;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public void setPassword(String password) {Password = password;
    }
}
