package com.example.demo;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             Scanner scanner = new Scanner(System.in, "UTF-8")) {

            System.out.println("已连接到聊天室服务器 " + SERVER_HOST + ":" + SERVER_PORT);

            // 接收消息线程
            Thread receiveThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("与服务器断开连接。");
                }
            });
            receiveThread.setDaemon(true);
            receiveThread.start();

            // 主线程发送消息
            while (true) {
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("已退出聊天室。");
                    break;
                }
                out.println(input);
            }
        } catch (IOException e) {
            System.out.println("无法连接到服务器：" + e.getMessage());
        }
    }
}