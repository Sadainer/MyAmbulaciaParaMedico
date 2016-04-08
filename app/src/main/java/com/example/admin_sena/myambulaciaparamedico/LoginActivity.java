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

    Context context;
    private static String DIR_URL = "http://190.109.185.138:8013/api/loginparamedico";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        CedulaView = (AutoCompleteTextView) findViewById(R.id.email);

        ContraseñaView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button login = (Button) findViewById(R.id.btnLogin);
        Button Registrar = (Button) findViewById(R.id.btnRegistro);
        final SharedPreferences registro = getSharedPreferences("prefs",MODE_PRIVATE);
        final String Cedulapref = registro.getString("Cedula", "2");
        final String contraseñapref = registro.getString("Contraseña","123");
        if(Cedulapref != "2"){
CedulaView.setText(Cedulapref);
        }


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
intentoLogin(Cedulapref,contraseñapref);
        }
});

    }


    private void intentoLogin(String Cedulapref,String contraseñapref) {
        //Reset Errors
        ContraseñaView.setError(null);
        CedulaView.setError(null);
        boolean cancel = false;
        View focusView = null;
String Contraseña = ContraseñaView.getText().toString();
        String Cedula= CedulaView.getText().toString();

// Crear Objeto loginDto con los datos que el usuario ingresó
        LoginDto login = new LoginDto();
        login.setPassword(ContraseñaView.getText().toString());
        login.setCedula(CedulaView.getText().toString());

        //////////// Enviar  Objeto al servidor, debe devolver un "Ok" en caso de que los datos sean correctos//////////

        /*PostAsyncrona EnviarLogin = new PostAsyncrona(loginjson.toJson(login), context, new PostAsyncrona.AsyncResponse() {

            @Override
    public void processFinish(String output) {
Toast.makeText(context,output.toString(),Toast.LENGTH_SHORT);
    }
});

        try {
            EnviarLogin.execute(DIR_URL).get();
            //System.out.println(resultado);
        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
        }
*/
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(Contraseña) && !contraseñaValida(Contraseña)){
            ContraseñaView.setError(getString(R.string.error_invalid_password));
            focusView =  ContraseñaView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(Cedula)) {
            CedulaView.setError(getString(R.string.error_field_required));
            focusView = CedulaView;
            cancel = true;
        } else if (!cedulaValida(Cedula)) {
            CedulaView.setError(getString(R.string.error_invalid_email));
            focusView = CedulaView;
            cancel = true;
        }else {
            if(Cedula.matches(Cedulapref)&& Contraseña.matches(contraseñapref)) {
                Log.e("ObjetoLoginDto",loginjson.toJson(login));
               EnviarLogin(login);
                // Cedula y contraseña validas, pasar a Mapas
  //              Intent w = new Intent(LoginActivity.this,MapsActivity.class);
    //            startActivity(w);
            }else { Toast.makeText(LoginActivity.this,"Cedula o contraseña no validas",Toast.LENGTH_SHORT).show(); }
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

        return pass.length() > 4;
    }


    private void EnviarLogin(LoginDto login){


        PostAsyncrona EnviarLogin = new PostAsyncrona(loginjson.toJson(login), context, new PostAsyncrona.AsyncResponse() {

            @Override
    public void processFinish(String output) {
Toast.makeText(context,output.toString(),Toast.LENGTH_SHORT);
    }
});

        try {
            EnviarLogin.execute(DIR_URL).get();
            //System.out.println(resultado);
        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
        }

    }

/*

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }
    //


    /**
     * Shows the progress UI and hides the login form.
     */


 /*   @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

*/
}

