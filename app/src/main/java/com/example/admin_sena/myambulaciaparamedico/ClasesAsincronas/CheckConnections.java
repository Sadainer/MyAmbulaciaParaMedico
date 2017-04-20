package com.example.admin_sena.myambulaciaparamedico.ClasesAsincronas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;

import com.example.admin_sena.myambulaciaparamedico.LoginActivity;

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
