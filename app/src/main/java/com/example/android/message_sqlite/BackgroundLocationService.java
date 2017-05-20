package com.example.android.message_sqlite;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundLocationService extends Service {
    public BackgroundLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){



    }



}
