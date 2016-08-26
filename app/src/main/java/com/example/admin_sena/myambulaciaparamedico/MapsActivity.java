package com.example.admin_sena.myambulaciaparamedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.Dto.UbicacionPacienteDto;
import com.example.admin_sena.myambulaciaparamedico.servicios.ServiceSignalR;
import com.example.admin_sena.myambulaciaparamedico.servicios.ServicioMyAmbu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context cnt;
    Marker marcadorAmbulancia;
    MyReceiver myReceiver;
    MyReceiverSignalR receiverSignalR;
   // private String url_Directions_API ="http://maps.googleapis.com/maps/api/directions/json?";
    private LatLng latLngAmbu;
    private LatLng latLngPaciente;
    private Clinica clinicaAsignada;
    FirebaseDatabase database;
    DatabaseReference reference;
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
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("");

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

    private class MyReceiver extends BroadcastReceiver{
        //Recibo Mi posicion

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            double la = arg1.getDoubleExtra("LatAmbu",0);
            double ln = arg1.getDoubleExtra("LngAmbu",0);
       //     Toast.makeText(MapsActivity.this,String.valueOf(la)+" " + String.valueOf(ln),Toast.LENGTH_SHORT).show();
            latLngAmbu = new  LatLng(la,ln);
//            CrearMarcador(latLng,"Ambulancia");
            if (marcadorAmbulancia!=null){
                marcadorAmbulancia.remove();
                marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                        .position(latLngAmbu)
                        .title("MiPosicion").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance3)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAmbu));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAmbu, 14.0f));
            }else {

                marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                        .position(latLngAmbu)
                        .title("MiPosicion").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance3)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAmbu));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAmbu, 14.0f));
            }

        }
    }

    private class MyReceiverSignalR extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            String mensaje = arg1.getStringExtra("UbicacionPaciente");
            final UbicacionPacienteDto ubicacionPacienteDto= (UbicacionPacienteDto)arg1.getExtras().getSerializable("dto");

            if (ubicacionPacienteDto != null) {
                Log.e("Direccion Paciente",ubicacionPacienteDto.getDireccion());
                latLngPaciente = new LatLng(ubicacionPacienteDto.getLatitud(),ubicacionPacienteDto.getLongitud());
                Marker marcador =  mMap.addMarker(new MarkerOptions()
                        .position(latLngPaciente)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.info_box, null);
                        TextView tvInfo = (TextView)v.findViewById(R.id.tvInfo);
                        tvInfo.setText("Direccion: "+ubicacionPacienteDto.getDireccion()+"\n"+"Pacientes: "+
                                String.valueOf(ubicacionPacienteDto.getNumeroPacientes()));
                        return v;
                    }
                });
                marcador.setTitle(ubicacionPacienteDto.getDireccion());
                marcador.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPaciente));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPaciente, 14.0f));
                buscarClinica(latLngPaciente);
            }
            reference.child("Pedidos").child("Pedido"+ubicacionPacienteDto.getIdPaciente()).child("Cancelado").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Toast.makeText(MapsActivity.this,"PEDIDO CANCELADOOOOO",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (mensaje!=null){
                Log.e("Mensaje recibido: ",mensaje);
            }else{
            }

        }
    }

    private void buscarClinica(LatLng latLngPaciente) {
        Location location = new Location("");
        location.setLongitude(latLngPaciente.longitude);
        location.setLatitude(latLngPaciente.latitude);

        ListaClinicas lista = new ListaClinicas();
        ArrayList<Float> a = new ArrayList<>();

        for (int i=0; i < lista.listaClinicas.size();i++ ){
            a.add(location.distanceTo(lista.listaClinicas.get(i).getUbicacion()));
        }
        for (int i=0; i < a.size();i++ ){

            Log.e("distancia: ",String.valueOf(a.get(i)));
        }
        int j = a.indexOf(Collections.min(a));
        Log.e("el menor es: ",String.valueOf(a.get(j)));

        clinicaAsignada = lista.listaClinicas.get(j);
        Toast.makeText(MapsActivity.this, "Clinica asignada: " + clinicaAsignada.getNombre() + "\n" + "Direccion: " + clinicaAsignada.getDireccion(),
                Toast.LENGTH_LONG).show();
    }

}
