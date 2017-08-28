package com.example.gildo.quadcoptercontroller.services.control;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.gildo.quadcoptercontroller.models.entities.threads.control.AttitudeControlThread;

/**
 * Created by gildo on 23/08/16.
 */
public class AttitudeControlService extends Service {
    public static final String INTENT_FILTER_ATTITUDE_CONTROL_SERVICE = "attitudeControlService";
    public static final String ATC_OUTPUT_ROTOR1_PW_MICROSECS_KEY = "atc_outputRotor1_pulseWidthMicrosecs";
    public static final String ATC_OUTPUT_ROTOR2_PW_MICROSECS_KEY = "atc_outputRotor2_pulseWidthMicrosecs";
    public static final String ATC_OUTPUT_ROTOR3_PW_MICROSECS_KEY = "atc_outputRotor3_pulseWidthMicrosecs";
    public static final String ATC_OUTPUT_ROTOR4_PW_MICROSECS_KEY = "atc_outputRotor4_pulseWidthMicrosecs";

    private AttitudeControlThread attitudeControlThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.attitudeControlThread = new AttitudeControlThread(this.getApplicationContext());
        this.attitudeControlThread.start();

        return Service.START_STICKY;
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the transferFunction</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (this.attitudeControlThread != null) {
            this.attitudeControlThread.setRunning(false);
        }
        super.onDestroy();
    }
}
