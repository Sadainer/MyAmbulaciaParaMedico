package com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sadainer Hernandez on 09/03/2015.
 * Clase para consumir servicios rest mediante el metodo GET
 */
public class GetAsyncrona extends AsyncTask<String, Void, String> {

    URL url;
    HttpURLConnection urlConnection;
    StringBuilder total;

    public void execute() {
        // TODO Auto-generated method stub
    }

    //Variable ruta se guarda la URI del servicio GET a consumir
    @Override
    protected String doInBackground(String... ruta) {

        try {
            url = new URL(ruta[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return total.toString();
    }

    public void onPostExecute(String result) {
        super.onPostExecute(result);
        //Se retorna un string que contiene un JSON con los datos obtenidos
    }
}
