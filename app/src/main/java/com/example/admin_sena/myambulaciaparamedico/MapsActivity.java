package com.example.admin_sena.myambulaciaparamedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.Dto.UbicacionPacienteDto;
import com.example.admin_sena.myambulaciaparamedico.rutas.DirectionFinder;
import com.example.admin_sena.myambulaciaparamedico.rutas.PasarUbicacion;
import com.example.admin_sena.myambulaciaparamedico.rutas.Route;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, PasarUbicacion {

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
    UbicacionPacienteDto ubicacionPacienteDto;
    String idAmbulancia;
    private List<Polyline> polylinePaths = new ArrayList<>();


    Toast toastClinica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        startService(new Intent(MapsActivity.this, ServiceSignalR.class));
        startService(new Intent(MapsActivity.this, ServicioMyAmbu.class));
        cnt = this;
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
        switch (item.getItemId()) {
            case R.id.opt_dibujar_ruta:
                if (latLngAmbu != null && latLngPaciente != null){
                    DirectionFinder rutasRequest = new DirectionFinder(this, latLngAmbu, latLngPaciente);
                    rutasRequest.peticionRutas();
                }else {
                    Toast.makeText(MapsActivity.this, "Ningún servicio activo", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.opt_cerrar_sesion:
                SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("ImLoggedIn", false);
                editor.apply();
                stopService(new Intent(MapsActivity.this, ServicioMyAmbu.class));
                stopService(new Intent(MapsActivity.this, ServiceSignalR.class));
                Intent volver_a_login = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(volver_a_login);
                finish();

                break;
            case R.id.opt_terminar_servicio:
                if (ubicacionPacienteDto!=null){
                    mMap.clear();
                    ubicacionPacienteDto = null;
                    startService(new Intent(MapsActivity.this, ServiceSignalR.class));
                    dibujarMarcador();
                }else {
                    Toast.makeText(MapsActivity.this, "Ningún servicio activo", Toast.LENGTH_SHORT).show();
                }

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
       // mMap.setMyLocationEnabled(true);


    }

    @Override
    public void trazarRutas(List<Route> rutas) {
        Log.e("Trazar rutas","trazando rutas");
        for (Route route : rutas) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        //Recibo Mi posicion

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            //Se ha actualizado la posicion de la ambulancia
            double la = arg1.getDoubleExtra("LatAmbu",0);
            double ln = arg1.getDoubleExtra("LngAmbu",0);
            idAmbulancia = arg1.getStringExtra("IdAmbulancia");
            latLngAmbu = new  LatLng(la,ln);
            if (marcadorAmbulancia!=null){
                marcadorAmbulancia.setPosition(latLngAmbu);
            }
            else {
                dibujarMarcador();
            }
        }
    }

    private class MyReceiverSignalR extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            String mensaje = arg1.getStringExtra("UbicacionPaciente");
            ubicacionPacienteDto= (UbicacionPacienteDto)arg1.getExtras().getSerializable("dto");

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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPaciente, 14.5f));
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

    private void dibujarMarcador(){
        marcadorAmbulancia =    mMap.addMarker(new MarkerOptions()
                .position(latLngAmbu)
                .title("MiPosicion").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance3)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngAmbu));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAmbu, 14.0f));
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
//        toastClinica.setText("Clinica asignada: " + clinicaAsignada.getNombre() + "\n" + "Direccion: " + clinicaAsignada.getDireccion());
  //      toastClinica.setDuration(Toast.LENGTH_SHORT);
        //toastClinica.setGravity(Gravity.BOTTOM,0,0);

        Toast.makeText(MapsActivity.this, "Clinica asignada: " + clinicaAsignada.getNombre() + "\n" + "Direccion: " + clinicaAsignada.getDireccion(),
                Toast.LENGTH_LONG).show();
        //snackClinica.setText();
        clinicaAsignada.setIdPaciente(String.valueOf(ubicacionPacienteDto.getIdPaciente()));
        clinicaAsignada.setIdAmbulancia(idAmbulancia);

        reference.child("Clinicas").child(clinicaAsignada.getNombre()).setValue(clinicaAsignada);


    }


}
