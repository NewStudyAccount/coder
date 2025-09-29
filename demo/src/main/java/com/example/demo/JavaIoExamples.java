package com.example.demo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Arrays;

public class JavaIoExamples {

    // 1. FileInputStream/FileOutputStream 读写二进制文件
    public static void fileInputStreamExample(String src, String dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }
    }

    // 2. FileReader/FileWriter 读写文本文件
    public static void fileReaderWriterExample(String src, String dest) throws IOException {
        try (FileReader fr = new FileReader(src);
             FileWriter fw = new FileWriter(dest)) {
            char[] buffer = new char[1024];
            int len;
            while ((len = fr.read(buffer)) != -1) {
                fw.write(buffer, 0, len);
            }
        }
    }

    // 3. BufferedReader/BufferedWriter 读写文本文件（带缓冲）
    public static void bufferedReaderWriterExample(String src, String dest) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(src));
             BufferedWriter bw = new BufferedWriter(new FileWriter(dest))) {
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // 4. DataInputStream/DataOutputStream 读写基本数据类型
    public static void dataStreamExample(String file) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.writeInt(123);
            dos.writeDouble(3.14);
            dos.writeUTF("Hello IO");
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            int i = dis.readInt();
            double d = dis.readDouble();
            String s = dis.readUTF();
            System.out.println("Read: " + i + ", " + d + ", " + s);
        }
    }

    // 5. 对象序列化与反序列化
    public static void objectStreamExample(String file) throws IOException, ClassNotFoundException {
        Person person = new Person("Tom", 18);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(person);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Person p = (Person) ois.readObject();
            System.out.println("Deserialized: " + p);
        }
    }

    // 6. RandomAccessFile 随机读写
    public static void randomAccessFileExample(String file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.writeInt(100);
            raf.writeUTF("Random");
            raf.seek(0);
            int i = raf.readInt();
            String s = raf.readUTF();
            System.out.println("RandomAccessFile: " + i + ", " + s);
        }
    }

    // 7. NIO Files/Path/Channels
    public static void nioFilesExample(String src, String dest) throws IOException {
        Path srcPath = Paths.get(src);
        Path destPath = Paths.get(dest);
        Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
        byte[] allBytes = Files.readAllBytes(srcPath);
        System.out.println("NIO read bytes: " + Arrays.toString(allBytes));
    }

    public static void nioChannelExample(String src, String dest) throws IOException {
        try (FileChannel inChannel = new FileInputStream(src).getChannel();
             FileChannel outChannel = new FileOutputStream(dest).getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (inChannel.read(buffer) != -1) {
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
        }
    }

    // 8. PrintStream/PrintWriter
    public static void printStreamWriterExample(String file) throws IOException {
        try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
            ps.println("PrintStream line 1");
            ps.println("PrintStream line 2");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println("PrintWriter line 3");
            pw.println("PrintWriter line 4");
        }
    }

    // 辅助类：序列化对象
    public static class Person implements Serializable {
        private String name;
        private int age;
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + '}';
        }
    }
}