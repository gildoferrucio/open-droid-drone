package com.example.gildo.quadcoptercontroller.models.entities.threads.communication;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.activities.ManualControlActivity;
import com.example.gildo.quadcoptercontroller.models.entities.EquilibriumAxis;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.services.communication.UDPConnectionService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by gildo on 30/10/16.
 */
public class UDPServerThread extends Thread {
    private int serverPort;
    private int payloadSize_bytes;
    private DatagramSocket datagramSocket;

    private LocalBroadcastManager localBroadcastManager;

    private boolean running;

    public UDPServerThread(int serverPort, int payloadSize_bytes, LocalBroadcastManager localBroadcastManager) {
        super();
        this.serverPort = serverPort;
        this.payloadSize_bytes = payloadSize_bytes;
        this.localBroadcastManager = localBroadcastManager;

        this.setName("UDPServerThread");
    }

    public void setRunning(boolean running) {
        if(!running){
            //Set all inputs to zero
            broadcastToControlServices(0, 0, 0, 0, false, false, true);
        }

        this.running = running;
    }

    @Override
    public void run() {
        DatagramPacket datagramPacket;
        this.running = true;

        try {
            byte[] buffer = new byte[this.payloadSize_bytes];
            this.datagramSocket = new DatagramSocket(this.serverPort);

            while (this.running) {
                // receive request
                datagramPacket = new DatagramPacket(buffer, buffer.length);
                // setting the timeout to zero means no timeout
                this.datagramSocket.setSoTimeout(0);
                this.datagramSocket.receive(datagramPacket);     //this code block the program flow
                extractAndSendPayload(datagramPacket);

                // send the response to the client at "address" and "port"
//                InetAddress address = datagramPacket.getAddress();
//                int port = datagramPacket.getPort();
//
//                String dString = new Date().toString() + "\n"
//                        + "Your address " + address.toString() + ":" + String.valueOf(port);
//                buffer = dString.getBytes();
//                datagramPacket = new DatagramPacket(buffer, buffer.length, address, port);
//                this.datagramSocket.send(datagramPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (this.datagramSocket != null) {
                this.datagramSocket.close();
            }
        }
    }

    private void extractAndSendPayload(DatagramPacket datagramPacket) {
        float desiredAxisRotationX_degrees, desiredAxisRotationY_degrees, desiredAxisRotationZ_degrees;
        float desiredThrottle_percentage;
        boolean executeTakeOffLandingManouver, executeGetCloserManouver, executeEmergencyStopManouver;
        byte[] payload, currentFloatWord, currentByteWord;
        int startIndex, endIndex = 0;
        byte currentByte;

        desiredAxisRotationX_degrees = desiredAxisRotationY_degrees = desiredAxisRotationZ_degrees = 0;
        desiredThrottle_percentage = 0;

        payload = datagramPacket.getData();

        for (int i = 0; i < 4; i++) {
            startIndex = i * 4;
            //The endIndex got to be exclusive, this means it won't be copied
            endIndex = (i * 4) + 4;
            currentFloatWord = Arrays.copyOfRange(payload, startIndex, endIndex);
            switch (i) {
                case 0:
                    desiredAxisRotationX_degrees = ByteBuffer.wrap(currentFloatWord).getFloat();
                    break;
                case 1:
                    desiredAxisRotationY_degrees = ByteBuffer.wrap(currentFloatWord).getFloat();
                    break;
                case 2:
                    desiredAxisRotationZ_degrees = ByteBuffer.wrap(currentFloatWord).getFloat();
                    break;
                case 3:
                    desiredThrottle_percentage = ByteBuffer.wrap(currentFloatWord).getFloat();
                    break;
            }
        }

        startIndex = endIndex;
        endIndex++;
        currentByteWord = Arrays.copyOfRange(payload, startIndex, endIndex);
        currentByte = ByteBuffer.wrap(currentByteWord).get();
        if (currentByte == 1) {
            executeTakeOffLandingManouver = true;
        } else {
            executeTakeOffLandingManouver = false;
        }

        startIndex = endIndex;
        endIndex++;
        currentByteWord = Arrays.copyOfRange(payload, startIndex, endIndex);
        currentByte = ByteBuffer.wrap(currentByteWord).get();
        if (currentByte == 1) {
            executeGetCloserManouver = true;
        } else {
            executeGetCloserManouver = false;
        }

        startIndex = endIndex;
        endIndex++;
        currentByteWord = Arrays.copyOfRange(payload, startIndex, endIndex);
        currentByte = ByteBuffer.wrap(currentByteWord).get();
        if (currentByte == 1) {
            executeEmergencyStopManouver = true;
        } else {
            executeEmergencyStopManouver = false;
        }

        broadcastToControlServices(desiredAxisRotationX_degrees, desiredAxisRotationY_degrees,
                desiredAxisRotationZ_degrees, desiredThrottle_percentage, executeTakeOffLandingManouver,
                executeGetCloserManouver, executeEmergencyStopManouver);
    }

    // send remote controller inputs with localBroadcasts to control services
    private void broadcastToControlServices(float desiredAxisRotationX_degrees, float desiredAxisRotationY_degrees,
                                            float desiredAxisRotationZ_degrees, float desiredThrottle_percentage,
                                            boolean executeTakeOffLandingManouver, boolean executeGetCloserManouver,
                                            boolean executeEmergencyStopManouver) {
        Intent broadcastIntent = new Intent(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE);

        if(executeEmergencyStopManouver){
            desiredThrottle_percentage = 0;
        }

        broadcastIntent.putExtra(EquilibriumAxis.DESIRED_ROTATION_X_DEGREES_KEY, desiredAxisRotationX_degrees);
        broadcastIntent.putExtra(EquilibriumAxis.DESIRED_ROTATION_Y_DEGREES_KEY, desiredAxisRotationY_degrees);
        broadcastIntent.putExtra(EquilibriumAxis.DESIRED_ROTATION_Z_DEGREES_KEY, desiredAxisRotationZ_degrees);

        broadcastIntent.putExtra(DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY, desiredThrottle_percentage);

        broadcastIntent.putExtra(ManualControlActivity.START_STOP_ROTORS_KEY,
                executeTakeOffLandingManouver);

        broadcastIntent.putExtra(ManualControlActivity.EXECUTE_GET_CLOSER_MANOUVER_KEY, executeGetCloserManouver);

        broadcastIntent.putExtra(ManualControlActivity.EXECUTE_EMERGENCY_STOP_MANOUVER_KEY, executeEmergencyStopManouver);

        this.localBroadcastManager.sendBroadcast(broadcastIntent);
    }
}
