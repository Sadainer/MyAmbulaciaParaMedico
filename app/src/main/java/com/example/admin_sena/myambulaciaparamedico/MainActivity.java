package com.example.admin_sena.myambulaciaparamedico;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.GetAsyncrona;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //    Intent i = new Intent(this, ServicioMyAmbu.class);
      //  this.startService(i);
        //System.out.println("Servicio Iniciado");
      Toast.makeText(this,"Servicio iniciado",Toast.LENGTH_SHORT).show();
        Intent a = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(a);
//        try {
//            String Res = new GetAsyncrona().execute("http://rest-service.guides.spring.io/greeting").get();
//            Toast.makeText(this,Res.toString(),Toast.LENGTH_LONG).show();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }


    }
}
