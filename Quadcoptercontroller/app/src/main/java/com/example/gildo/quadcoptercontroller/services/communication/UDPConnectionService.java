package com.example.gildo.quadcoptercontroller.services.communication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.activities.ManualControlActivity;
import com.example.gildo.quadcoptercontroller.models.entities.EquilibriumAxis;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.threads.communication.UDPClientThread;
import com.example.gildo.quadcoptercontroller.models.entities.threads.communication.UDPServerThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by gildo on 20/10/16.
 */
public class UDPConnectionService extends Service {
    public static final String INTENT_FILTER_UDP_CONNECTION_SERVICE = "udpConnectionService";

    private static final String STANDARD_SERVER_IP = "192.168.0.10";
    private static final int STANDARD_SERVER_PORT = 7171;
    public static final int PAYLOAD_SIZE_BYTES = (4 * (Float.SIZE / Byte.SIZE)) + (3 * (Byte.SIZE / Byte.SIZE));

    private Context context;

    private boolean actAsClient;

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver;

    private String serverIP;

    private int serverPort;

    private UDPClientThread udpClientThread;

    private UDPServerThread udpServerThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences;
        IntentFilter intentFilter;
        this.context = this.getApplicationContext();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.actAsClient = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_act_as_client), false);
        if (this.actAsClient) {
            this.serverIP = sharedPreferences.getString(this.context.getString(R.string.pref_key_server_ipv4_address),
                    UDPConnectionService.STANDARD_SERVER_IP);
        }
        this.serverPort = sharedPreferences.getInt(this.context.getString(R.string.pref_key_server_port),
                UDPConnectionService.STANDARD_SERVER_PORT);

        if (this.actAsClient) {
            // register broadcastReceiver of inputs from ManualControllerActivity
            this.broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    float desiredAxisRotationX_degrees, desiredAxisRotationY_degrees,
                            desiredAxisRotationZ_degrees;
                    float desiredThrottle_percentage;
                    boolean executeTakeOffLandingManouver, executeGetCloserManouver, executeEmergencyStopManouver;
                    ByteArrayOutputStream payload;

                    desiredAxisRotationX_degrees = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_X_DEGREES_KEY, 0);
                    desiredAxisRotationY_degrees = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_Y_DEGREES_KEY, 0);
                    desiredAxisRotationZ_degrees = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_Z_DEGREES_KEY, 0);

                    desiredThrottle_percentage = intent.getFloatExtra(
                            DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY, 0);

                    executeTakeOffLandingManouver = intent.getBooleanExtra(
                            ManualControlActivity.START_STOP_ROTORS_KEY, false);

                    executeGetCloserManouver = intent.getBooleanExtra(
                            ManualControlActivity.EXECUTE_GET_CLOSER_MANOUVER_KEY, false);

                    executeEmergencyStopManouver = intent.getBooleanExtra(
                            ManualControlActivity.EXECUTE_EMERGENCY_STOP_MANOUVER_KEY, false);

                    //Fill payload with inputs...
                    payload = new ByteArrayOutputStream();
                    try {
                        /* If another item is added to the payload, you must update the constant
                         * PAYLOAD_SIZE_BYTES with the quantity of info you want to send on each package.
                         * Actual payload quantity = 4 Floats and 3 Bytes
                         */
                        payload.write(ByteBuffer.allocate(Float.SIZE / Byte.SIZE).putFloat(
                                desiredAxisRotationX_degrees).array());
                        payload.write(ByteBuffer.allocate(Float.SIZE / Byte.SIZE).putFloat(
                                desiredAxisRotationY_degrees).array());
                        payload.write(ByteBuffer.allocate(Float.SIZE / Byte.SIZE).putFloat(
                                desiredAxisRotationZ_degrees).array());

                        payload.write(ByteBuffer.allocate(Float.SIZE / Byte.SIZE).putFloat(
                                desiredThrottle_percentage).array());

                        payload.write(ByteBuffer.allocate(Byte.SIZE / Byte.SIZE).put(
                                (byte)(executeTakeOffLandingManouver ? 1 : 0)).array());

                        payload.write(ByteBuffer.allocate(Byte.SIZE / Byte.SIZE).put(
                                (byte)(executeGetCloserManouver? 1 : 0)).array());

                        payload.write(ByteBuffer.allocate(Byte.SIZE / Byte.SIZE).put(
                                (byte)(executeEmergencyStopManouver? 1 : 0)).array());

                        //TODO: send location of getCloserManouver!!!
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // send inputs via udp packets
                    udpClientThread = new UDPClientThread(serverIP, serverPort, payload.toByteArray());
                    udpClientThread.start();
                }
            };

            intentFilter = new IntentFilter();
            intentFilter.addAction(ManualControlActivity.INTENT_FILTER_MANUAL_CONTROL_ACTIVITY);
            this.localBroadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);
        } else {
            // create server thread, to receive remote controller inputs via udp packets
            this.udpServerThread = new UDPServerThread(STANDARD_SERVER_PORT, PAYLOAD_SIZE_BYTES,
                    this.localBroadcastManager);
            this.udpServerThread.start();
        }

        //Reference: http://www.vogella.com/tutorials/AndroidServices/article.html#services-and-background-processing

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        if((!this.actAsClient) && (this.udpServerThread != null)){
            this.udpServerThread.setRunning(false);
        }
        super.onDestroy();
    }

//    private String getIpAddress() {
//        Enumeration<NetworkInterface> enumNetworkInterfaces;
//        Enumeration<InetAddress> enumInetAddress;
//        InetAddress inetAddress;
//        String ipV4 = "";
//
//        try {
//            enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
//
//            while (enumNetworkInterfaces.hasMoreElements()) {
//                enumInetAddress = enumNetworkInterfaces.nextElement().getInetAddresses();
//
//                while (enumInetAddress.hasMoreElements()) {
//                    inetAddress = enumInetAddress.nextElement();
//
//                    // for getting IPV4 format
//                    if ((!inetAddress.isLoopbackAddress()) && (inetAddress instanceof Inet4Address)) {
////                        ipV4 = inetAddress.getHostAddress().toString();
//                        ipV4 = inetAddress.getHostAddress();
//                        return ipV4;
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//
//        return ipV4;
//    }
}
