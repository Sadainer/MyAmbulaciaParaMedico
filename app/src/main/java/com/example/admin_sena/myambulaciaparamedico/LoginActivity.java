package com.example.admin_sena.myambulaciaparamedico;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
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
import android.view.Menu;
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
    private static String DIR_URL = "http://190.109.185.138:8013/api/loginparamedicos";
    public ProgressDialog progressDialog;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulogin, menu);
        return true;
    }

    private void intentoLogin(final LoginDto loginDto) {
        //Reset Errors
        ContraseñaView.setError(null);
        CedulaView.setError(null);
        boolean cancel = false;
        View focusView = null;
        String Cedula= CedulaView.getText().toString();
        progressDialog = new ProgressDialog(LoginActivity.this);

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
        } else if (Cedula.length()<5) {
            CedulaView.setError(getString(R.string.error_invalid_email));
            focusView = CedulaView;
            cancel = true;
        }else { //Ningun error con el login hasta ahora, se procede a validar la informacion en el servidor
            Log.e("Antesdeenviar", loginjson.toJson(loginDto));


            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    EnviarLogin(loginDto);
                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);

            progressDialog.setTitle("Enviando");
            progressDialog.setMessage("Por favor espere");
            progressDialog.show();
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

    }



    private void EnviarLogin(final LoginDto login){
        PostAsyncrona EnviarLogin = new PostAsyncrona(loginjson.toJson(login), LoginActivity.this, new PostAsyncrona.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                Toast.makeText(LoginActivity.this,output.toString(),Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if(output!="Error"){     //////////Si no hay errores////////
                    progressDialog.dismiss();
                    /////Convertir Json a LoginDto
                    LoginDto loginExitoso = loginjson.fromJson(output,LoginDto.class);
                    //Asignar Id
                    String IdRecibido = loginExitoso.getCedula();
                    //Asignar Contraseña
                    String Pass = loginExitoso.getPassword();
                    //Guardarlas en Sharedpreferences
                    editor.putString("IdAmbulancia",IdRecibido).putString("Password",Pass).putBoolean("ImLoggedIn", true);
                    editor.commit();
                    LoginActivity.this.startService(new Intent(LoginActivity.this, ServicioMyAmbu.class));
                    finish();
                    Intent k = new Intent(LoginActivity.this,MapsActivity.class);
                    startActivity(k);
                    //Iniciar Servicio
                    Intent s = new Intent(LoginActivity.this,ServicioMyAmbu.class);
                    startService(s);

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

