package com.example.admin_sena.myambulaciaparamedico;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.myambulaciaparamedico.Dto.UbicacionDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;


public class ServicioMyAmbu extends Service {

    Context cnt;

    //Variables gestion Ubicacion
    private LocationManager locationMangaer = null;
    //Variable para guardar al mejor proveedor para obtener la ubicacion
    String MejorProveedor=null;
    // Variables de URI del servicio
    private static String DIR_URL = "http://190.109.185.138:8013/api/Ubicacionambulancias";
    //Variable que controla el tiempo en que se actualiza la ubicacion en segundos
    private static int TIEMPO_ACTUALIZACION=10000;
    //Variable que controla la actualizacion del radio de movimiento de la ambulancia en metros
    private static int RADIO_ACTUALIZACION=10;
    //Listener de ubicacion
    private LocationListener locationListener = null;


    final Gson gsson = new Gson();

    NotificationManager nm ;

    public ServicioMyAmbu() {
    }



    @Override
    public void onCreate() {
        super.onCreate();
        cnt= getApplicationContext();
        System.out.println("Servicio Iniciado");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationMangaer = (LocationManager) getSystemService(cnt.LOCATION_SERVICE);
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Criteria req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_FINE);
        req.setAltitudeRequired(true);

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
           // Intent d =new Intent(ServicioMyAmbu.this,MapsActivity.class);
           // d.putExtra("LatAmbulancia",posicionActual.getLatitude());
         //   d.putExtra("LngAmbulancia",posicionActual.getLongitude());

       //     sendBroadcast(d);
           Log.e("broadcast enviado",String.valueOf(posicionActual.getLatitude()));
            EnviarUbicacion(posicionActual);
        }

        locationListener = new MiUbicacion();
        locationMangaer.requestLocationUpdates(MejorProveedor, TIEMPO_ACTUALIZACION, RADIO_ACTUALIZACION, locationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //Metodo para enviar Ubicacion al servidor
    private void EnviarUbicacion(Location location){

        UbicacionDto ubicacion = new UbicacionDto();
        SharedPreferences prefs= getSharedPreferences("preferences",MODE_PRIVATE);
        //ubicacion.setIdAmbulancia(prefs.getString("IdAmbulancia", "1"));
        ubicacion.setIdAmbulancia("1");
        ubicacion.setLatitud(location.getLatitude());
        ubicacion.setLongitud(location.getLongitude());
        Log.e("Envio Posicion",gsson.toJson(ubicacion) );
        PostAsyncrona EnviarUbicacion = new PostAsyncrona(gsson.toJson(ubicacion), cnt, new PostAsyncrona.AsyncResponse() {
            @Override
            public void processFinish(String output) {

            }
        });
        System.out.println(gsson.toJson(ubicacion));
        try {
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




    //Clase que permite escuchar las ubicaciones, cada vez que cambia la ubicacion se activa el metodo onLocationChanged y creamos un
    //nuevo marcador con la ubicacion y como titulo la hora del registro de la ubicacion
    private class MiUbicacion implements LocationListener
    {
        int notificationID = 1;
        @Override
        public void onLocationChanged(Location location) {

            EnviarUbicacion(location);
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
}
