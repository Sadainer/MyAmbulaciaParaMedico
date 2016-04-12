package com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.admin_sena.myambulaciaparamedico.Dto.RegistroDto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
public class PostAsyncrona extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    private String mData = null;
    URL url;
    HttpURLConnection connection;
    Context cnt;
    private ProgressDialog prgEnviando;


    public PostAsyncrona(String data, Context context, AsyncResponse delegate) {
        mData = data;
        cnt= context;
        this.delegate = delegate;
        prgEnviando = new ProgressDialog(context);
    }
    public void execute() {
        // TODO Auto-generated method stub

    }


    //Variable ruta se guarda la URI del servicio GET a consumir


    @Override
    protected void onPreExecute() {


        this.prgEnviando.setTitle("MyAmbu");
        this.prgEnviando.setMessage("Enviando ...");
        this.prgEnviando.show();

    }

    @Override
    protected String doInBackground(String... params) {
        String mensajeRespuesta = "";
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

            //Read
            StringBuilder sb = null;
            BufferedReader br = null;
            //here is the problem

            int responseCode=connection.getResponseCode();
            Log.e("respondeCode",String.valueOf(responseCode));
            if(responseCode==HttpURLConnection.HTTP_ACCEPTED || responseCode==HttpURLConnection.HTTP_OK
                    || responseCode==HttpURLConnection.HTTP_CREATED ){
                String line;
                sb = new StringBuilder();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                br = new BufferedReader(isr);

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                mensajeRespuesta = sb.toString();
            }else{
                mensajeRespuesta= "Error";
            }


        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("IOException");
            e.printStackTrace();
        }
        return mensajeRespuesta;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
        this.prgEnviando.dismiss();
    }
}
