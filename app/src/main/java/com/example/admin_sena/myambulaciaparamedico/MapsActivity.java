package com.example.admin_sena.myambulaciaparamedico;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.Dto.UbicacionPacienteDto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context cnt;
    Marker marcadorAmbulancia;
    MyReceiver myReceiver;
    MyReceiverSignalR receiverSignalR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        startService(new Intent(MapsActivity.this, ServiceSignalR.class));
        startService(new Intent(MapsActivity.this,ServicioMyAmbu.class));


        cnt=this;
        mapFragment.getMapAsync(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumapas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.opt_cerrar_sesion:
                SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("ImLoggedIn", false);
                editor.commit();
                stopService(new Intent(MapsActivity.this,ServicioMyAmbu.class));
                Intent volver_a_login = new Intent(MapsActivity.this,LoginActivity.class);
                startActivity(volver_a_login);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ServicioMyAmbu.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        receiverSignalR = new MyReceiverSignalR();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(ServiceSignalR.MY_ACTION2);
        registerReceiver(receiverSignalR, intentFilter2);


        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        unregisterReceiver(myReceiver);
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void CrearMarcador(LatLng location, String Titulo)
    {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(Titulo));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            double la = arg1.getDoubleExtra("LatAmbu",0);
            double ln = arg1.getDoubleExtra("LngAmbu",0);
            LatLng latLng = new LatLng(la,ln);
//            CrearMarcador(latLng,"Ambulancia");
            if (marcadorAmbulancia!=null){
                marcadorAmbulancia.remove();
                marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("MiPosicion"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
            }else {
                marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("MiPosicion"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
            }

        }
    }

    private class MyReceiverSignalR extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            String mensaje = arg1.getStringExtra("UbicacionPaciente");
            UbicacionPacienteDto ubicacionPacienteDto= (UbicacionPacienteDto)arg1.getExtras().getSerializable("dto");

            if (ubicacionPacienteDto != null) {
            LatLng    latLngPaciente = new LatLng(ubicacionPacienteDto.getLatitud(),ubicacionPacienteDto.getLongitud());
                mMap.addMarker(new MarkerOptions()
                        .position(latLngPaciente)
                        .title("Paciente"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPaciente));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPaciente, 14.0f));
            }

            if (mensaje!=null){
                Toast.makeText(MapsActivity.this,"ubicacion recibida en maps: "+mensaje,Toast.LENGTH_SHORT).show();
                Log.e("Mensaje recibido: ",mensaje);
            }else{
                Toast.makeText(MapsActivity.this,"Mensaje nulo",Toast.LENGTH_SHORT).show();
            }

        }
    }


}

