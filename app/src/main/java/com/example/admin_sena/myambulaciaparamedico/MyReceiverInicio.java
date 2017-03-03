package com.example.admin_sena.myambulaciaparamedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.admin_sena.myambulaciaparamedico.servicios.ServiceSignalR;
import com.example.admin_sena.myambulaciaparamedico.servicios.ServicioMyAmbu;


public class MyReceiverInicio extends BroadcastReceiver {
    public MyReceiverInicio() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        Log.e("Llego el intent","OnReceive");
//        throw new UnsupportedOperationException("Not yet implemented");
        context.startService(new Intent(context, ServicioMyAmbu.class));
     //   context.startService(new Intent(context, ServiceSignalR.class));

    }
}
