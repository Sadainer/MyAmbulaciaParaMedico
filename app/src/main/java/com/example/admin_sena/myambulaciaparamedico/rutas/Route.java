package com.example.admin_sena.myambulaciaparamedico.rutas;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by oscar on 6/12/16.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
