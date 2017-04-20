package com.example.admin_sena.myambulaciaparamedico.servicios;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.myambulaciaparamedico.Dto.UbicacionDto;
import com.example.admin_sena.myambulaciaparamedico.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;


public class ServicioMyAmbu extends Service implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    Context cnt;

    //My_Action
    public final static String MY_ACTION = "MY_ACTION";
    FirebaseDatabase database;
    DatabaseReference reference, miAmbulancia;
    UbicacionDto ubicacion = new UbicacionDto();
    final Gson gsson = new Gson();
    Intent intent2;
    NotificationManager nm;
    Location myLocation;

    private GoogleApiClient client;
    double LatAmbu, LngAmbu;

    public ServicioMyAmbu() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cnt = getApplicationContext();
        System.out.println("Servicio Iniciado");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("");
        if (client == null) {
            client = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }
    @Override
    public void onStart(Intent intent, int startId) {
        client.connect();
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return super.onStartCommand(intent, flags, startId);

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //Metodo para enviar Ubicacion al servidor
    private void EnviarUbicacion(Location location) {
        intent2 = new Intent();
        intent2.setAction(MY_ACTION);
        LatAmbu = location.getLatitude();
        LngAmbu = location.getLongitude();
        intent2.putExtra("LatAmbu", LatAmbu).putExtra("LngAmbu", LngAmbu).putExtra("IdAmbulancia",ubicacion.getIdAmbulancia());
        sendBroadcast(intent2);
        ubicacion.setLatitud(location.getLatitude());
        ubicacion.setLongitud(location.getLongitude());
        Log.e("Envio Posicion", gsson.toJson(ubicacion));
        try {
            if (ubicacion.getIdAmbulancia()!=null){


                reference.child("Ambulancias").child(ubicacion.getIdAmbulancia()).child("latitud").setValue(location.getLatitude());
                reference.child("Ambulancias").child(ubicacion.getIdAmbulancia()).child("longitud").setValue(location.getLongitude());
            }else{

            }

        }catch (Exception e){
            Log.e("Excepción: ",e.getMessage());

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        while (myLocation == null){
            try {
                myLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            }catch (Exception e){
                Log.e("Servicio my ambu","mylocation es nula");
            }
        }

        if (myLocation != null) {

            SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);
            ubicacion.setIdAmbulancia(prefs.getString("IdAmbulancia", "1"));
            ubicacion.setLatitud(myLocation.getLatitude());
            ubicacion.setLongitud(myLocation.getLongitude());
            reference.child("Ambulancias").child(ubicacion.getIdAmbulancia()).setValue(ubicacion);

           // Log.e("broadcast enviado", String.valueOf(posicionActual.getLatitude()));
            EnviarUbicacion(myLocation);
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("location ","changed");
                EnviarUbicacion(location);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        miAmbulancia = reference.child("Ambulancias").child(ubicacion.getIdAmbulancia());
        miAmbulancia.removeValue();
        client.disconnect();
    }
}
