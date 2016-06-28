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

    Gson Registrojson = new Gson();
    private static String DIR_URL = "http://190.109.185.138:8013/api/paramedicos";
    Context cnt;
    Button btnRegistro;
    EditText edtNuevaContraseña;
    EditText edtNombres;
    EditText edtApellidos;
    EditText edtCedula;
    EditText edtCorreo;
    EditText edtConfirmarContraseña;
    EditText edtConfirmarCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);

        cnt=this;
        btnRegistro = (Button)findViewById(R.id.btnRegistrarse);
        edtNombres = (EditText)findViewById(R.id.edtNombres);
        edtApellidos =(EditText)findViewById(R.id.edtApellidos);
        edtCedula = (EditText)findViewById(R.id.edtCedula);
        edtCorreo = (EditText)findViewById(R.id.edtCorreo);
        edtConfirmarCorreo = (EditText)findViewById(R.id.edtConfirmarCorreo);
        edtNuevaContraseña = (EditText)findViewById(R.id.edtNuevaContraseña);
        edtConfirmarContraseña = (EditText)findViewById(R.id.edtRepetircontraseña);


        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((edtCorreo.getText().toString().equals(edtConfirmarCorreo.getText().toString())) &&
                        (edtNuevaContraseña.getText().toString().equals(edtConfirmarContraseña.getText().toString())) )
                {
                    RegistroDto registro = new RegistroDto();
                    registro.setNombres(edtNombres.getText().toString());
                    registro.setCedula(edtCedula.getText().toString());
                    registro.setApellidos(edtApellidos.getText().toString());
                    registro.setCorreo(edtCorreo.getText().toString());
                    registro.setPassword(edtNuevaContraseña.getText().toString());

                    /////Enviar registro al servidor aqui
                    EnviarRegistro(registro);

                    Log.e("Registto",Registrojson.toJson(registro));
                }else{
                    if (!edtCorreo.getText().toString().equals(edtConfirmarCorreo.getText().toString())){
                        edtConfirmarCorreo.setFocusable(true);
                        Toast.makeText(cnt,"los correos no son iguales",Toast.LENGTH_SHORT).show();
                    }else if (!edtNuevaContraseña.getText().toString().equals(edtConfirmarContraseña.getText().toString())){
                        edtConfirmarContraseña.setFocusable(true);
                        Toast.makeText(cnt,"las contraseñas no son iguales",Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

public void EnviarRegistro (RegistroDto registroDto){

    Log.e("Prueba", Registrojson.toJson(registroDto));
    PostAsyncrona Enviar = new PostAsyncrona(Registrojson.toJson(registroDto), cnt, new PostAsyncrona.AsyncResponse() {
        @Override
        public void processFinish(String output) {
            Log.e("output", output);
            if (!output.equals("Error")) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                RegistroDto paramedico = Registrojson.fromJson(output, RegistroDto.class);
                editor.putString("Nombres", paramedico.getNombres());
                editor.putString("Apellidos", paramedico.getApellidos());
                editor.putString("Cedula", paramedico.getCedula());
                editor.putString("Correo", paramedico.getCorreo());
                editor.putString("Contraseña", paramedico.getPassword());
                editor.commit();
                finish();
                //Volver al login
                Intent volver_a_login = new Intent(Registro2.this, LoginActivity.class);
                startActivity(volver_a_login);

            }
        }
    });
    try {
        Enviar.execute(DIR_URL).get();
    } catch (InterruptedException e) {
        System.out.println("Error i");
        e.printStackTrace();
    } catch (ExecutionException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

}

}
