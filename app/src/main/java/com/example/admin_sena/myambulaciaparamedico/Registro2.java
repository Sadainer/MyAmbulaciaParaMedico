package com.example.admin_sena.myambulaciaparamedico;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registro2 extends AppCompatActivity {
    String Nombres;
    String Apellidos;
    String Correo;
    String ConfirmarCorreo;
    String Contraseña;
    String ConfirmarContraseña;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        Button btnRegistro = (Button)findViewById(R.id.btnRegistrarse);
        final EditText edtNombres = (EditText)findViewById(R.id.edtNombres);
        final EditText edtApellidos = (EditText)findViewById(R.id.edtapellidos);
        final EditText edtCorreo = (EditText)findViewById(R.id.edtCorreo);
        final EditText edtConfirmarCorreo = (EditText)findViewById(R.id.edtConfirmarCorreo);
        final EditText edtNuevaContraseña = (EditText)findViewById(R.id.edtNuevaContraseña);
        final EditText edtConfirmarContraseña = (EditText)findViewById(R.id.edtRepetircontraseña);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nombres = edtNombres.getText().toString();
                Apellidos = edtApellidos.getText().toString();
                Correo = edtCorreo.getText().toString();
                ConfirmarCorreo = edtConfirmarCorreo.getText().toString();
                Contraseña = edtNuevaContraseña.getText().toString();
                ConfirmarContraseña = edtConfirmarContraseña.getText().toString();
            }
        });

    }
}
