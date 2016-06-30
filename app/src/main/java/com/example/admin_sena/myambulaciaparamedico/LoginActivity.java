package com.example.admin_sena.myambulaciaparamedico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.myambulaciaparamedico.Dto.LoginDto;
import com.example.admin_sena.myambulaciaparamedico.Servicios.ServiceSignalR;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView CedulaView;
    private EditText ContraseñaView;
    private View mProgressView;
    private View mLoginFormView;
    private Gson loginjson = new Gson();
    public SharedPreferences registro;
    private ServiceSignalR mService;
    private boolean mBound = false;
    Context context;
    private static String DIR_URL = "http://190.109.185.138:8013/api/loginparamedicos";
    Button login;
    Button Registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final SharedPreferences registro = getSharedPreferences("prefs",MODE_PRIVATE);
        setContentView(R.layout.activity_login);

        registro = getSharedPreferences("preferences",MODE_PRIVATE);
        if (registro.getBoolean("ImLoggedIn",false)){
             //Si ya he iniciado sesion
//          this.startService(new Intent(this, ServicioMyAmbu.class));
            Intent c = new Intent(LoginActivity.this,MapsActivity.class);
            startActivity(c);
            //Iniciar Servicio

            finish();

        }
        // Set up the login form.
        CedulaView = (AutoCompleteTextView) findViewById(R.id.email);

        ContraseñaView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        login = (Button) findViewById(R.id.btnLogin);
        Registrar = (Button) findViewById(R.id.btnRegistro);

       Registrar.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent pasar_a_registro = new Intent(LoginActivity.this,Registro2.class);
               startActivity(pasar_a_registro);
           }
       });

        login.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginDto login = new LoginDto();
            login.setPassword(ContraseñaView.getText().toString());
            login.setCedula(CedulaView.getText().toString());
            intentoLogin(login);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulogin, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.opt_registro:
                Intent pasar_a_registro = new Intent(LoginActivity.this,Registro2.class);
                startActivity(pasar_a_registro);
        }
        return super.onOptionsItemSelected(item);
    }

    private void intentoLogin(LoginDto loginDto) {
        //Reset Errors
        ContraseñaView.setError(null);
        CedulaView.setError(null);
        boolean cancel = false;
        View focusView = null;
        String Cedula= CedulaView.getText().toString();


            // Crear Objeto loginDto con los datos que el usuario ingresó
        //////////// Enviar  Objeto al servidor, debe devolver un "Ok" en caso de que los datos sean correctos//////////


        // Verifica que la variable password no este vacia
        if (TextUtils.isEmpty(loginDto.getPassword())){
            ContraseñaView.setError(getString(R.string.error_invalid_password));
            focusView =  ContraseñaView;
            cancel = true;
        }

        // Valida si el usuario introdujo cedula
        else if (TextUtils.isEmpty(loginDto.getCedula())) {
            CedulaView.setError(getString(R.string.error_field_required));
            focusView = CedulaView;
            cancel = true;
        } else if (Cedula.length()<3) {
            CedulaView.setError(getString(R.string.error_invalid_email));
            focusView = CedulaView;
            cancel = true;
        }else { //
            Log.e("Antesdeenviar", loginjson.toJson(loginDto));

            EnviarLogin(loginDto);
            //No hubo errores
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

    }

    private void EnviarLogin(final LoginDto login){
        PostAsyncrona EnviarLogin = new PostAsyncrona(loginjson.toJson(login), context, new PostAsyncrona.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                //Toast.makeText(LoginActivity.this,output.toString(),Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if(!output.equals("Error")){     //////////Si no hay errores////////
                    /////Convertir Json a LoginDto
                    LoginDto loginExitoso = loginjson.fromJson(output, LoginDto.class);
                    //Asignar Id
                    String IdRecibido = loginExitoso.getCedula();
                    //Asignar Contraseña
                    String Pass = loginExitoso.getPassword();
                    //Guardarlas en Sharedpreferences
                    editor.putString("IdAmbulancia",IdRecibido).putString("Password",Pass).putBoolean("ImLoggedIn", true);
                    editor.apply();

                    finish();

                    startActivity(new Intent(LoginActivity.this, MapsActivity.class));

                }
                else{
                    Toast.makeText(LoginActivity.this,"Contraseña o Usuario no validos",Toast.LENGTH_SHORT).show();
                }

            }
});

        try {
            EnviarLogin.execute(DIR_URL).get();
            Log.e("ObjetoLoginDto", loginjson.toJson(login));
        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
