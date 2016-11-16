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

    //Variable para guardar al mejor proveedor para obtener la ubicacion
    String MejorProveedor = null;
    //My_Action
    public final static String MY_ACTION = "MY_ACTION";
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference miAmbulancia;
    UbicacionDto ubicacion = new UbicacionDto();
    final Gson gsson = new Gson();
    Intent intent2;
    NotificationManager nm;
    Location myLocation;

    double la;

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

        LocationManager locationMangaer = (LocationManager) getSystemService(cnt.LOCATION_SERVICE);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Criteria req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_FINE);


        //Mejor proveedor por criterio
        MejorProveedor = locationMangaer.getBestProvider(req, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
        }
        //Obtenemos la ultima ubicacion registrada en el equipo
        Location posicionActual = locationMangaer.getLastKnownLocation(MejorProveedor);

        //Si la posicion es diferente de null creamos un marcados con el titulo Posicion inicial
        if (posicionActual != null) {
            System.out.println(posicionActual.getLatitude() + "   " + posicionActual.getLongitude());

            LatAmbu = posicionActual.getLatitude();
            LngAmbu = posicionActual.getLongitude();


            SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);
            ubicacion.setIdAmbulancia(prefs.getString("IdAmbulancia", "1"));
            ubicacion.setLatitud(LatAmbu);
            ubicacion.setLongitud(LngAmbu);
            reference.child("Ambulancias").child(ubicacion.getIdAmbulancia()).setValue(ubicacion);

            Log.e("broadcast enviado", String.valueOf(posicionActual.getLatitude()));
            EnviarUbicacion(posicionActual);
        }

        LocationListener locationListener = new MiUbicacion();

        int RADIO_ACTUALIZACION = 5;
        int TIEMPO_ACTUALIZACION = 19000;
        locationMangaer.requestLocationUpdates(MejorProveedor, TIEMPO_ACTUALIZACION, RADIO_ACTUALIZACION, locationListener);

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
        intent2.putExtra("LatAmbu", LatAmbu).putExtra("LngAmbu", LngAmbu).putExtra("IdAmbulancia",ubicacion.getIdAmbulancia());
        sendBroadcast(intent2);
        ubicacion.setLatitud(location.getLatitude());
        ubicacion.setLongitud(location.getLongitude());
        Log.e("Envio Posicion", gsson.toJson(ubicacion));
        try {
            if (ubicacion.getIdAmbulancia()!=null){

                Log.e("IdAbulancia","no nula");
                reference.child("Ambulancias").child(ubicacion.getIdAmbulancia()).child("latitud").setValue(location.getLatitude());
                reference.child("Ambulancias").child(ubicacion.getIdAmbulancia()).child("longitud").setValue(location.getLongitude());
            }else{

            }

        }catch (Exception e){
            Log.e("Excepción: ",e.getMessage());

        }
        PostAsyncrona EnviarUbicacion = new PostAsyncrona(gsson.toJson(ubicacion), cnt, new PostAsyncrona.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.e("Posicion ", " enviada al servidor");
            }
        });

        System.out.println(gsson.toJson(ubicacion));
        try {
            String DIR_URL = "http://190.109.185.138:8013/api/Ubicacionambulancias";
            EnviarUbicacion.execute(DIR_URL).get();
            System.out.println("Ok");
        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
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


    //Clase que permite escuchar las ubicaciones, cada vez que cambia la ubicacion se activa el metodo onLocationChanged y creamos un
    //nuevo marcador con la ubicacion y como titulo la hora del registro de la ubicacion
    private class MiUbicacion implements LocationListener
    {
        int notificationID = 1;
        @Override
        public void onLocationChanged(Location location) {
           // Log.e("Posicion "," cambiada");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            nm.cancel(notificationID);
        }

        @Override
        public void onProviderDisabled(String provider) {

            displayNotification();

        }

        // Notifica sobre GPS desactivado y envia para activacion
        protected void displayNotification(){

            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
            myIntent.putExtra("notificationID", notificationID);

            PendingIntent pendingIntent = PendingIntent.getActivity(cnt, 0, myIntent, 0);


            CharSequence ticker ="Activar GPS";
            CharSequence contentTitle = "MyAmbu";
            CharSequence contentText = "GPS Desactivado";
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification noti = new NotificationCompat.Builder(cnt)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(ticker)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(R.drawable.ic_setting_dark, ticker, pendingIntent)
                    .setVibrate(new long[] {100, 250, 100, 500})
                    .setSound(alarmSound)
                    .build();
            nm.notify(notificationID, noti);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        miAmbulancia = reference.child("Ambulancias").child(ubicacion.getIdAmbulancia());
        miAmbulancia.removeValue();
        client.disconnect();
    }
}
