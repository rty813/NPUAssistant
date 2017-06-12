package com.npu.zhang.npuassistant.Model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AXDataService extends Service {
    public AXDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
