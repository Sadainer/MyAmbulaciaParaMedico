package com.example.admin_sena.myambulaciaparamedico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.myambulaciaparamedico.Dto.RegistroDto;
import com.google.gson.Gson;

public class Registro2 extends AppCompatActivity {



    String Nombres;
    String Apellidos;
    String Cedula;
    String Correo;
    String ConfirmarCorreo;
    String Contraseña;
    String ConfirmarContraseña;
    Gson Registrojson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        Button btnRegistro = (Button)findViewById(R.id.btnRegistrarse);
        final EditText edtNombres = (EditText)findViewById(R.id.edtNombres);
        final EditText edtApellidos =(EditText)findViewById(R.id.edtApellidos);
        final EditText edtCedula = (EditText)findViewById(R.id.edtCedula);
        final EditText edtCorreo = (EditText)findViewById(R.id.edtCorreo);
        final EditText edtConfirmarCorreo = (EditText)findViewById(R.id.edtConfirmarCorreo);
        final EditText edtNuevaContraseña = (EditText)findViewById(R.id.edtNuevaContraseña);
        final EditText edtConfirmarContraseña = (EditText)findViewById(R.id.edtRepetircontraseña);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                Nombres = edtNombres.getText().toString();
                Apellidos = edtApellidos.getText().toString();
                Cedula = edtCedula.getText().toString();
                Correo = edtCorreo.getText().toString();
                ConfirmarCorreo = edtConfirmarCorreo.getText().toString();
                Contraseña = edtNuevaContraseña.getText().toString();
                ConfirmarContraseña = edtConfirmarContraseña.getText().toString();

                editor.putString("Nombres", Nombres);
                editor.putString("Apellidos",Apellidos);
                editor.putString("Cedula",Cedula);
                editor.putString("Correo",Correo);
                editor.putString("Contraseña",Contraseña);
                editor.commit();
                RegistroDto NuevoRegistro = new RegistroDto();
                NuevoRegistro.setNombres(Nombres);
                NuevoRegistro.setApellidos(Apellidos);
                NuevoRegistro.setCorreo(Correo);
                NuevoRegistro.setCedula(Cedula);
                NuevoRegistro.setContraseña(Contraseña);

    //            PostAsyncrona EnviarRegistro = new PostAsyncrona()

                Intent volver_a_login = new Intent(Registro2.this,LoginActivity.class);
                startActivity(volver_a_login);
            }
        });

    }





}
