package com.example.demo;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 8888;
    // 存储所有客户端输出流，用于广播
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        System.out.println("聊天室服务端启动，端口：" + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);
        try {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("新客户端连接：" + client.getRemoteSocketAddress());
                new Thread(new ClientHandler(client)).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                clientWriters.add(out);
                out.println("欢迎加入聊天室！");

                String msg;
                while ((msg = in.readLine()) != null) {
                    broadcast("[" + socket.getRemoteSocketAddress() + "]: " + msg);
                }
            } catch (IOException e) {
                System.out.println("客户端断开：" + socket.getRemoteSocketAddress());
            } finally {
                if (out != null) clientWriters.remove(out);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}