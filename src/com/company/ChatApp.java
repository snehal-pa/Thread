package com.company;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatApp {
    Scanner input = new Scanner(System.in);
    private NetworkClient nc;
    private NetworkServer ns;

    public ChatApp(NetworkClient nc) {
        this.nc = nc;
        ns = NetworkServer.get();
        nc.start();
        ns.start();
        start();
    }

    public void start() {
        Thread send = new Thread(this::send, "send");
        send.start();

        Thread rcv = new Thread(this::receive, "receive");
        rcv.start();
    }

    private void send() {
        while (true) {
            System.out.println("Write your message");
            String msg = input.nextLine();
            nc.addToSendQueue(msg);

        }

    }

    private void receive() {
        String serverMsg = nc.getFromReceiveQueue();
        System.out.println("Received from Server: " + "\"" + serverMsg + "\" len: " + serverMsg.length());

    }


}
