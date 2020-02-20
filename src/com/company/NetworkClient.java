package com.company;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkClient implements Runnable {
    private final String SERVER_IP ="192.168.0.166";
    private final int MSG_SIZE = 512;
    private final int SLEEP_MS = 100;

    private DatagramSocket socket;
    private InetAddress serverAddress;
    AtomicBoolean isRunning = new AtomicBoolean(true);

    private ArrayBlockingQueue<String> sendQueue = new ArrayBlockingQueue<>(100);

    private ArrayBlockingQueue<String> rcvQueue = new ArrayBlockingQueue<>(100);


    public NetworkClient() {
        try {
            serverAddress = InetAddress.getByName(SERVER_IP);

            socket = new DatagramSocket();
            socket.setSoTimeout(SLEEP_MS);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMsgToServer(String msg) {
        byte[] buffer = msg.getBytes();
        DatagramPacket request = new DatagramPacket(buffer, buffer.length, this.serverAddress, NetworkServer.get().PORT);
        try {
            socket.send(request);
        } catch (Exception e) {
        }
    }

    private void receiveMessageFromServer() {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(response);
            String serverMsg = new String(buffer, 0, response.getLength());
            System.out.println("client recieved: "+ serverMsg);
            addToReceiveQueue(serverMsg);

            // TODO: Save the msg to a queue instead
        } catch (Exception ex) {
            try {
                Thread.sleep(SLEEP_MS);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void run() {
        //System.out.println("Client: current thread is: " + Thread.currentThread().getName());
        while (isRunning.get()) {
            String threadName = Thread.currentThread().getName();
            if (threadName.equals("send")) {
                if (sendQueue.size() != 0)
                    try {
                        String msg = sendQueue.take();
                        sendMsgToServer(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


            } else if (threadName.equals("receive")) {
                receiveMessageFromServer();
            }

        }


    }

    public void start() {
        Thread send = new Thread(this, "send");
        send.start();

        Thread rcv = new Thread(this, "receive");
        rcv.start();
    }

    public void addToSendQueue(String s) {
        try {
            sendQueue.put(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addToReceiveQueue(String s) {
        try {
            rcvQueue.put(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getFromReceiveQueue() {
        try {
            return getRcvQueue().take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayBlockingQueue<String> getRcvQueue() {
        return rcvQueue;
    }
}

