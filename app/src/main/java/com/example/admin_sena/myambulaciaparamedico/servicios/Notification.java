package com.example.admin_sena.myambulaciaparamedico.servicios;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by oscar on 30/03/17.
 */

public class Notification {

    private Vibrator v;
    private Uri noti;
    private Ringtone r;
    public Notification(Context c) {
        v = (Vibrator)c.getSystemService(VIBRATOR_SERVICE);

        noti = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(c, noti);
    }

    public void sonar() {

        v.vibrate(400);
        r.play();

    }


}
