package com.example.admin_sena.myambulaciaparamedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.Dto.UbicacionPacienteDto;
import com.example.admin_sena.myambulaciaparamedico.Servicios.ServiceSignalR;
import com.example.admin_sena.myambulaciaparamedico.Servicios.ServicioMyAmbu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context cnt;
    Marker marcadorAmbulancia;
    MyReceiver myReceiver;
    MyReceiverSignalR receiverSignalR;
    private String url_Directions_API ="http://maps.googleapis.com/maps/api/directions/json?";
    private LatLng latLngAmbu;
    private LatLng latLngPaciente;
    private TextView txtInfoPedido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        startService(new Intent(MapsActivity.this, ServiceSignalR.class));
        startService(new Intent(MapsActivity.this, ServicioMyAmbu.class));

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
            case R.id.opt_dibujar_ruta:

                break;
            case R.id.opt_cerrar_sesion:
                SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("ImLoggedIn", false);
                editor.apply();
                stopService(new Intent(MapsActivity.this, ServicioMyAmbu.class));
                stopService(new Intent(MapsActivity.this, ServiceSignalR.class));
                Intent volver_a_login = new Intent(MapsActivity.this,LoginActivity.class);
                startActivity(volver_a_login);

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

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
        unregisterReceiver(receiverSignalR);
        finish();
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
        //Recibo Mi posicion

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            double la = arg1.getDoubleExtra("LatAmbu",0);
            double ln = arg1.getDoubleExtra("LngAmbu",0);
            latLngAmbu = new  LatLng(la,ln);
//            CrearMarcador(latLng,"Ambulancia");
            if (marcadorAmbulancia!=null){
                marcadorAmbulancia.remove();
                marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                        .position(latLngAmbu)
                        .title("MiPosicion"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAmbu));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAmbu, 14.0f));
            }else {
                marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                        .position(latLngAmbu)
                        .title("MiPosicion"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAmbu));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAmbu, 14.0f));
            }

        }
    }

    private class MyReceiverSignalR extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            String mensaje = arg1.getStringExtra("UbicacionPaciente");
            UbicacionPacienteDto ubicacionPacienteDto= (UbicacionPacienteDto)arg1.getExtras().getSerializable("dto");

            if (ubicacionPacienteDto != null) {
                latLngPaciente = new LatLng(ubicacionPacienteDto.getLatitud(),ubicacionPacienteDto.getLongitud());
                mMap.addMarker(new MarkerOptions()
                        .position(latLngPaciente)
                        .title("Paciente"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPaciente));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPaciente, 14.0f));
                txtInfoPedido.setVisibility(View.VISIBLE);
                txtInfoPedido.setText("Direccion: "+ubicacionPacienteDto.getDireccion()+" IdServicio "+ubicacionPacienteDto.getIdPaciente()+ " Tipo de emergencia: "+ ubicacionPacienteDto.getTipoemergencia());
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

