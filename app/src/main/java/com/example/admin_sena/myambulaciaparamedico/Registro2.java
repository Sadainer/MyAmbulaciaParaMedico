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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Registro2 extends AppCompatActivity {

    Gson Registrojson = new Gson();
    private static String DIR_URL = "http://myambulancia1.azurewebsites.net/api/paramedicos";
    Context cnt;
    Button btnRegistro;
    EditText edtNuevaContraseña, edtNombres, edtApellidos, edtCedula;
    EditText edtCorreo, edtConfirmarContraseña, edtConfirmarCorreo;
    FirebaseDatabase database;
    DatabaseReference registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        database = FirebaseDatabase.getInstance();
        registros = database.getReference("RegistrosAmbulancias");

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

    public void EnviarRegistro (final RegistroDto registroDto){

    PostAsyncrona Enviar = new PostAsyncrona(Registrojson.toJson(registroDto), cnt, new PostAsyncrona.AsyncResponse() {
        @Override
        public void processFinish(String output) {
            Log.e("output", output);
            if (output.equals("ErrorA")){
                Toast.makeText(cnt,"La Ambulancia ya existe.",Toast.LENGTH_SHORT).show();
            }else if(output.equals("\"Paramedico Guardado\"")){


                registros.child(registroDto.getCedula()).setValue(registroDto);
                DatabaseReference ultimoregistro = registros.child(registroDto.getCedula());
                ultimoregistro.child("NumServicios").setValue(0);

                Toast.makeText(cnt,"Ambulancia registrada exitosamente.",Toast.LENGTH_SHORT).show();
                finish();
                //Volver al login
                Intent volver_a_login = new Intent(Registro2.this, LoginActivity.class);
                startActivity(volver_a_login);
            }
            else {
                Toast.makeText(cnt,"No se pudo registrar la ambulancia, por favor intente mas tarde.",Toast.LENGTH_SHORT).show();
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
