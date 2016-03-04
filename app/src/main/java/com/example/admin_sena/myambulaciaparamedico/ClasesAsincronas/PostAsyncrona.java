package com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Sadainer Hernandez on 09/03/2015.
 * Clase para consumir servicios rest mediante el metodo GET
 */
public class PostAsyncrona extends AsyncTask<String, Void, Void> {

    private String mData = null;
    URL url;
    HttpURLConnection connection;
    Context cnt;

    public PostAsyncrona(String data, Context context) {
        mData = data;
        cnt= context;
    }
    public void execute() {
        // TODO Auto-generated method stub
    }


    //Variable ruta se guarda la URI del servicio GET a consumir

    @Override
    protected Void doInBackground(String... params) {
        String mensajeRespuesta = null;
        try {


            url = new URL(params[0]);

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(mData);
            dStream.flush();
            dStream.close();
            mensajeRespuesta = connection.getResponseMessage();


            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();

            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();
            System.out.println("output===============" + mensajeRespuesta);

        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("IOException");
            e.printStackTrace();
        }
        return null;
    }

    public void onPostExecute(Void result) {
        super.onPostExecute(result);
        //Se retorna un string que contiene un JSON con los datos obtenidos
    }
}
