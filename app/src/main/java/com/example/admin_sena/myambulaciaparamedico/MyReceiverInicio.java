package com.example.admin_sena.myambulaciaparamedico;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class MyReceiverInicio extends BroadcastReceiver {
    public MyReceiverInicio() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        context.startService(new Intent(context, ServicioMyAmbu.class));

    }
}
