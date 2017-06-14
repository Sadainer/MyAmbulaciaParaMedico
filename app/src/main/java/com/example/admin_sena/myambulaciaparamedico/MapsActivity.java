package com.example.admin_sena.myambulaciaparamedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.myambulaciaparamedico.Dto.ClinicaDto;
import com.example.admin_sena.myambulaciaparamedico.actividades.LoginActivity;
import com.example.admin_sena.myambulaciaparamedico.rutas.DirectionFinder;
import com.example.admin_sena.myambulaciaparamedico.rutas.PasarUbicacion;
import com.example.admin_sena.myambulaciaparamedico.rutas.Route;
import com.example.admin_sena.myambulaciaparamedico.servicios.Notification;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, PasarUbicacion {

    int cont=0;
    private GoogleMap mMap;
    Marker marcadorAmbulancia;
    MyReceiver myReceiver;
    private LatLng latLngAmbu, latLngPaciente;
    FirebaseDatabase database;
    DatabaseReference reference, pedido, ambulanciaFirebase, clinicasRef, currentEmergency;
    AlertDialog dialogAceptarEm;
    Location mylocation = new Location("point a"), clinicaLocation = new Location("point b");
    SimpleDateFormat sdf;
    String currentDateandTime;
    ListaClinicas lista;
    int j;
    Timer timer;
    TimerTask timerTask;
    String idAmbulancia, idPaciente;
    private List<Polyline> polylinePaths = new ArrayList<>();

    Clinica clinicaReal;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        startService(new Intent(MapsActivity.this, ServicioMyAmbu.class));
        mapFragment.getMapAsync(this);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("");
        currentEmergency = reference.child("CurrentEmergency");

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder
                .setIcon(R.drawable.ic_launcher2)
                .setTitle("Aceptar Emergencia?")
                .setCancelable(false)
                .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        pedido.setValue(true);
                        dialogInterface.dismiss();
                        // acepto la emergencia
                        reference.child("Ambulancias").child(idAmbulancia).child("ocupado").setValue(true);
                        handleEmergencia();
                    }
                })
                .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //no acepto emergencia
                    }
        });
        clinicasRef = database.getReference("Clinicas");
        dialogAceptarEm = builder.create();
        sdf = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss", Locale.US);
    }

    private void handleEmergencia() {
        ambulanciaFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              double latPedido  = (double)dataSnapshot.child("Pedido").child("latitud").getValue();
              double longPedido = (double)dataSnapshot.child("Pedido").child("longitud").getValue();
              String direction = (String)dataSnapshot.child("Pedido").child("direccion").getValue();
              String numPacientes =  Long.toString((Long) dataSnapshot.child("Pedido").child("numeroPacientes").getValue());
              idPaciente = (String)dataSnapshot.child("Pedido").child("idPaciente").getValue();
                latLngPaciente = new LatLng(latPedido, longPedido);
              Location locationPedido = new Location("");
                locationPedido.setLongitude(latPedido);
                locationPedido.setLongitude(longPedido);
                drawMarkerPedido(direction, numPacientes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawMarkerPedido(final String direction, final String numPacientes) {

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
                tvInfo.setText("Direccion: "+direction+"\n"+"Pacientes: "+
                        numPacientes);
                return v;
            }
        });
        marcador.setTitle(direction);
        marcador.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPaciente));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPaciente, 14.5f));
        buscarClinica(latLngPaciente);
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
            case R.id.opt_dibujar_ruta_clinica:
                if (clinicaReal != null){
                    DirectionFinder rutasRequest = new DirectionFinder(this, latLngAmbu, new LatLng(clinicaReal.getUbicacion().getLatitude(), clinicaReal.getUbicacion().getLongitude()));
                    rutasRequest.peticionRutas();
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(clinicaReal.getUbicacion().getLatitude(),
                                    clinicaReal.getUbicacion().getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital)));
                }else{
                    Toast.makeText(MapsActivity.this, "Ninguna clínica asignada.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.opt_cerrar_sesion:
                if (pedido != null){

                    Toast.makeText(this, "Solicitud vigente, no puede cerrar sesión.", Toast.LENGTH_SHORT).show();
                }else {

                    SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("ImLoggedIn", false);
                    editor.apply();
                    currentEmergency.removeValue();
                    stopService(new Intent(MapsActivity.this, ServicioMyAmbu.class));
                    Intent volver_a_login = new Intent(MapsActivity.this, LoginActivity.class);
                    startActivity(volver_a_login);
                    finish();
                }


                break;
            case R.id.opt_terminar_servicio:
                if (pedido!=null){
                    mMap.clear();
                    pedido = null;
                    dibujarMarcador();
                    currentDateandTime = sdf.format(new Date());
                    reference.child("Pedidos").child("Pedido:" + idPaciente).child("tiempos").child("4").setValue(currentDateandTime);

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
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub

        unregisterReceiver(myReceiver);
        //unregisterReceiver(receiverSignalR);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Permisos","no granted");

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
            mylocation.setLatitude(la);
            mylocation.setLongitude(ln);
            if (idAmbulancia == null){
                idAmbulancia = arg1.getStringExtra("IdAmbulancia");
                escucharporPedidos(idAmbulancia);
            }

            latLngAmbu = new  LatLng(la,ln);
            if (marcadorAmbulancia!=null){
                marcadorAmbulancia.setPosition(latLngAmbu);
            }
            else {
                dibujarMarcador();
            }
        }
    }

    private void escucharporPedidos(String idAmbulancia) {
        ambulanciaFirebase = reference.child("Ambulancias").child(idAmbulancia).getRef();
        reference.child("Ambulancias").child(idAmbulancia).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                cont++;
                Log.e("Contador ", String.valueOf(cont));
                Log.e("Key ", dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals("Pedido")){
                    if (MapsActivity.this.isFinishing()){
                        Log.e("Alert ","Activity is finishing");
                    }
                    pedido = dataSnapshot.child("aceptado").getRef();
                    try {
                        Notification noti = new Notification(getApplicationContext());
                        noti.sonar();
                        dialogAceptarEm.show();
                    }catch (WindowManager.BadTokenException e){
                        pedido.setValue(false);
                    }

                }
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

        lista = new ListaClinicas();
        ArrayList<Float> a = new ArrayList<>();

        for (int i=0; i < lista.listaClinicas.size();i++ ){
            a.add(location.distanceTo(lista.listaClinicas.get(i).getUbicacion()));
        }
        for (int i=0; i < a.size();i++ ){

            Log.e("distancia: ",String.valueOf(a.get(i)));
        }
        j = a.indexOf(Collections.min(a));
        Log.e("el menor es: ",String.valueOf(a.get(j)));

        Clinica clinicaAsignada = lista.listaClinicas.get(j);

        mostrarDialogoClinicaAsignada(clinicaAsignada);
        clinicaAsignada.setIdPaciente(idPaciente);
        clinicaAsignada.setIdAmbulancia(idAmbulancia);
        reference.child("Pedidos").child("Pedido:" + idPaciente).child("clinicaAsignada").setValue(clinicaAsignada.getNombre());

    }

    private void mostrarDialogoClinicaAsignada(final Clinica clinicaAsignada) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Su clinica asignada es " + clinicaAsignada.getNombre() + "\n" +
                "Realizar traslado a este centro de atención.")
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher2)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        clinicaReal = clinicaAsignada;
                        //reference.child("Clinicas").child(clinicaReal.getNombre()).setValue(clinicaReal);
                        ClinicaDto pedidoClinica = new ClinicaDto(idAmbulancia, idPaciente, clinicaReal.getUbicacion().getLongitude(), clinicaReal.getUbicacion().getLatitude());
                        reference.child("Clinicas").child(clinicaReal.getNombre()).child("Pedidos").child(idPaciente).setValue(pedidoClinica);
                        reference.child("Pedidos").child("Pedido:" + idPaciente).child("clinicaReal").setValue(clinicaReal.getNombre());
                        clinicaLocation = clinicaAsignada.getUbicacion();
                        startTimer();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        mostrarDialogSelectClinica(lista);
                    }
                });
        builder.create();
        builder.show();


    }

    private void mostrarDialogSelectClinica(final ListaClinicas lista) {

        lista.listaClinicas.remove(j);
        String[] nombres = lista.getNombres(lista.listaClinicas);

        final ArrayAdapter<String> adp = new ArrayAdapter<>(MapsActivity.this,
                android.R.layout.simple_spinner_item, nombres);

        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sp = new Spinner(MapsActivity.this);
        sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        sp.setAdapter(adp);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                clinicaReal = lista.listaClinicas.get(i);
                Log.e("Clinica Real ", clinicaReal.getNombre());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                clinicaReal = lista.listaClinicas.get(0);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Seleccionar CLinica");
        builder.setView(sp);
        builder.setCancelable(false)
                .setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//
//   reference.child("Clinicas").child(clinicaReal.getNombre()).setValue(clinicaReal);
                ClinicaDto pedidoClinica = new ClinicaDto(idAmbulancia, idPaciente, clinicaReal.getUbicacion().getLongitude(), clinicaReal.getUbicacion().getLatitude());
                reference.child("Clinicas").child(clinicaReal.getNombre()).child("Pedidos").child(idPaciente).setValue(pedidoClinica);
                reference.child("Pedidos").child("Pedido:" + idPaciente).child("clinicaReal").setValue(clinicaReal.getNombre());

                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //inicializa la tarea de TimerTask
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 6000, 15000); //
    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        float distancia = mylocation.distanceTo(clinicaLocation);
                        if (distancia<20.0){
                            // llego a la clinica
                            currentDateandTime = sdf.format(new Date());
                            reference.child("Pedidos").child("Pedido:" + idPaciente).child("tiempos").child("4").setValue(currentDateandTime);
                            Toast.makeText(MapsActivity.this, "TIempo 4 " + currentDateandTime, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        };

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MapsActivity.this, ServicioMyAmbu.class));
        database.goOffline();
        super.onDestroy();
    }
}
