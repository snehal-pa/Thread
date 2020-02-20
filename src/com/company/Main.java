package com.company;

public class Main {

    public static void main(String[] args) {

        NetworkClient c = new NetworkClient();

        ChatApp chatApp = new ChatApp(c);
    }
}
