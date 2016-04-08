package com.example.admin_sena.myambulaciaparamedico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.myambulaciaparamedico.Dto.RegistroDto;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class Registro2 extends AppCompatActivity {



    String Nombres;
    String Apellidos;
    String Cedula;
    String Correo;
    String ConfirmarCorreo;
    String Contraseña;
    String ConfirmarContraseña;
    Gson Registrojson = new Gson();
    private static String DIR_URL = "http://190.109.185.138:8013/api/paramedicos";
    Context context;
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
                RegistroDto registro = new RegistroDto();
                registro.setNombres(edtNombres.getText().toString());
                registro.setCedula(edtCedula.getText().toString());
                registro.setApellidos(edtApellidos.getText().toString());
                registro.setCorreo(edtCorreo.getText().toString());
                registro.setPassword(edtNuevaContraseña.getText().toString());



                editor.putString("Nombres", registro.getNombres());
                editor.putString("Apellidos", Apellidos);
                editor.putString("Cedula", registro.getCedula());
                editor.putString("Correo", Correo);
                editor.putString("Contraseña", registro.getPassword());
                editor.commit();

                /////Enviar registro al servidor aqui

                //EnviarRegistro(registro);

Log.e("RegistroDto",Registrojson.toJson(registro));
                //Volver al login
                //Intent volver_a_login = new Intent(Registro2.this,LoginActivity.class);
                //startActivity(volver_a_login);
            }
        });

    }

public void EnviarRegistro (RegistroDto registroDto){

    Log.e("Prueba", Registrojson.toJson(registroDto));
    PostAsyncrona Enviar = new PostAsyncrona(Registrojson.toJson(registroDto), context, new PostAsyncrona.AsyncResponse() {
        @Override
        public void processFinish(String output) {
Toast.makeText(context,"Registro Exitoso",Toast.LENGTH_SHORT).show();

        }
    });
    try {
        Enviar.execute(DIR_URL).get();
    } catch (InterruptedException e) {
        System.out.println("Error i");
        e.printStackTrace();
    } catch (ExecutionException e) {
        System.out.println("Error e");
        e.printStackTrace();
    }

}

}
