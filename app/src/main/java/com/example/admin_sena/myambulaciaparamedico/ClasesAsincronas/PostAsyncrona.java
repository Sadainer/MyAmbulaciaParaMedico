package com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostAsyncrona extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    private String mData = null;
    private HttpURLConnection connection;

    public PostAsyncrona(String data, AsyncResponse delegate) {
        mData = data;
        this.delegate = delegate;
    }
    public void execute() {
        // TODO Auto-generated method stub

    }
    //Variable ruta se guarda la URI del servicio GET a consumir

    @Override
    protected String doInBackground(String... params) {
        String mensajeRespuesta = "";
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(mData);
            dStream.flush();
            dStream.close();
            //Read
            StringBuilder sb;
            BufferedReader br;
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
                mensajeRespuesta= "ErrorA";
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
        connection.disconnect();
        delegate.processFinish(result);
        Log.e("postExecute","post");
    }
}
