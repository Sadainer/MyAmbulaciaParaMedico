package com.example.admin_sena.myambulaciaparamedico;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.myambulaciaparamedico.Dto.LoginDto;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
 //   private static final int REQUEST_READ_CONTACTS = 0;


   // private static final String[] DUMMY_CREDENTIALS = new String[]{
     //       "foo@example.com:hello", "bar@example.com:world"
   // };
   // private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView CedulaView;
    private EditText ContraseñaView;
    private View mProgressView;
    private View mLoginFormView;
    private Gson loginjson = new Gson();
    public SharedPreferences registro;
    Context context;
    private static String DIR_URL = "http://190.109.185.138:8013/api/loginparamedico";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final SharedPreferences registro = getSharedPreferences("prefs",MODE_PRIVATE);
        setContentView(R.layout.activity_login);

        registro = getSharedPreferences("preferences",MODE_PRIVATE);
        if (registro.getBoolean("ImLoggedIn",false)){
    //Si ya he iniciado sesion
            this.startService(new Intent(this, ServicioMyAmbu.class));
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
        Button login = (Button) findViewById(R.id.btnLogin);
        Button Registrar = (Button) findViewById(R.id.btnRegistro);



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


    private void intentoLogin(LoginDto loginDto) {
        //Reset Errors
        ContraseñaView.setError(null);
        CedulaView.setError(null);
        boolean cancel = false;
        View focusView = null;
        String Contraseña = ContraseñaView.getText().toString();
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
        } else if (!cedulaValida(Cedula)) {
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
//{"Cedula":"1065655757","Contraseña":"12345678"}
    private boolean cedulaValida(String email) {

                return email.contains("1");
    }

    private boolean contraseñaValida(String pass) {

        return pass.length() > 3;
    }

    private void EnviarLogin(final LoginDto login){
        PostAsyncrona EnviarLogin = new PostAsyncrona(loginjson.toJson(login), context, new PostAsyncrona.AsyncResponse() {

            @Override
    public void processFinish(String output) {
        Toast.makeText(LoginActivity.this,output.toString(),Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getSharedPreferences("preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


              //  Log.e("output",output);

       //         SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
         //       SharedPreferences.Editor editor = prefs.edit();
                //Quitar Llaves y comillas
                String output2 =output.replace("{","").replace("}", "").replace("\"", "");
                String[] output3 = output2.split(",");
                for (String str:output3) {
                Log.e("str", str);
                }
                String[] Cedula1 = output3[0].split(":");
                //Id de Ambulancia o Cedula del paramedico registrado
                String IdRecibido = Cedula1[1];
                String[] Pass = output3[4].split(":");
                // Password
                String Pass1 = Pass[1];

                Log.e("IdAmbulancia",IdRecibido);
                Log.e("Pass",Pass1);
                Double pass = Double.valueOf(Pass1);
                Double pass2 = pass+2;
                Log.e("Password + 2",String.valueOf(pass2));

                if (login.getCedula().matches(IdRecibido) && login.getPassword().matches(Pass1)){
                                   //Guardar en sharedPreferences IdAmbulancia y Password
                    editor.putString("IdAmbulancia",IdRecibido).putString("Password",Pass1).putBoolean("ImLoggedIn", true);
                    editor.commit();
                    LoginActivity.this.startService(new Intent(LoginActivity.this, ServicioMyAmbu.class));
                    Intent k = new Intent(LoginActivity.this,MapsActivity.class);
                    startActivity(k);
                    //Iniciar Servicio
                    Intent s = new Intent(LoginActivity.this,ServicioMyAmbu.class);
                    startService(s);
                    //context.startService(new Intent(, ServicioMyAmbu.class));
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

}

