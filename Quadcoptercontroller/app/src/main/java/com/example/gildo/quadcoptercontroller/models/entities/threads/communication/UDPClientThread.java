package com.example.gildo.quadcoptercontroller.models.entities.threads.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by gildo on 29/10/16.
 */
public class UDPClientThread extends Thread {
    String destinationIP;
    int destinationPort;
    byte[] payloadBuffer;

    public UDPClientThread(String destinationIP, int destinationPort, byte[] payloadBuffer) {
        super();
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.payloadBuffer = payloadBuffer;

        this.setName("UDPClientThread");
    }

    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        InetAddress destinationAddress;
        DatagramPacket datagramPacket;

        try {
            //Send message
            datagramSocket = new DatagramSocket();
            destinationAddress = InetAddress.getByName(this.destinationIP);
            datagramPacket = new DatagramPacket(this.payloadBuffer, this.payloadBuffer.length, destinationAddress,
                    this.destinationPort);
            datagramSocket.send(datagramPacket);

            //Get message
//            datagramPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
//            datagramSocket.receive(datagramPacket);
//            line = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }
}
