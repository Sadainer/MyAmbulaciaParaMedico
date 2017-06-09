package com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

/**
 * Created by oscar on 10/03/17.
 */

public class CheckConnections {
    LocationManager locationMangaer;
    ConnectivityManager connectivityManager;

    public CheckConnections(final Context c) {
        connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        locationMangaer = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

    }
    public boolean todoconectado() {

        return locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


}
