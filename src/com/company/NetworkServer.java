package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkServer implements Runnable {
    private static NetworkServer instance = new NetworkServer();
    public final int PORT = 80;
    private final int SLEEP_MS = 100;
    private final int MSG_SIZE = 512;
    private DatagramSocket socket;
    AtomicBoolean isRunning = new AtomicBoolean(true);

    // lista som sparar alla clienten
    List<SocketAddress> clients = new ArrayList<>();


    private NetworkServer() {
        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout(SLEEP_MS);
            System.out.println("Server starting on: " + socket.getLocalSocketAddress().toString());
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    public static NetworkServer get() {
        return instance;
    }


    public void sendMsgToClient(String msg, SocketAddress clientSocketAddress) {
        byte[] buffer = msg.getBytes();

        DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientSocketAddress);

        try {
            socket.send(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning.get()) {
                DatagramPacket clientRequest = new DatagramPacket(new byte[MSG_SIZE], MSG_SIZE);

                if (!receiveMsgFromAnyClient(clientRequest)) {
                    continue;
                }

                clients.add(clientRequest.getSocketAddress());

                String clientMsg = new String(clientRequest.getData(), 0, clientRequest.getLength());
                System.out.println("Server: received a msg: " + "\"" + clientMsg + "\"" + "  from client " + clientRequest.getSocketAddress().toString()); // debugging purpose only!
                //sendMsgToClient("Message received: ", clientRequest.getSocketAddress());

                for (SocketAddress sa : clients) {
                    sendMsgToClient("Forwarding " + clientMsg + " to all clients", sa);
                }

            }
        }

    public void start() {
        Thread rcv = new Thread(this, "receive");
        rcv.start();
    }

    private boolean receiveMsgFromAnyClient(DatagramPacket clientRequest) {
        try {
            socket.receive(clientRequest);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

}


