package chapter06;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class UDPServer {
    private int remotePort;
    private InetAddress remoteIP;
    private DatagramSocket serverDatagramSocket;//UDP套接字

    //用于接收数据的报文字节数组缓存最大容量，字节为单位
    private static final int MAX_PACKET_SIZE = 512;

    //private static final int MAX_PACKET_SIZE = 65507;
    public UDPServer() throws IOException {
        //创建一个UDP套接字，系统随机选定一个未使用的UDP端口绑定
        serverDatagramSocket = new DatagramSocket(8008);
        //设置接收数据超时
//    socket.setSoTimeout(30000);
    }

    //定义一个数据的发送方法
    public void send(String msg) {
        try {
            //将待发送的字符串转为字节数组
            byte[] outData = msg.getBytes("utf-8");
            //构建用于发送的数据报文，构造方法中传入远程通信方（服务器)的ip地址和端口
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, remoteIP, remotePort);
            //给UDPServer发送数据报
            serverDatagramSocket.send(outPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //定义数据接收方法
    public String receive() {
        String msg;
        //先准备一个空数据报文
        DatagramPacket inPacket = new DatagramPacket(
                new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
        try {
            //读取报文，阻塞语句，有数据就装包在inPacket报文中，装完或装满为止。
            serverDatagramSocket.receive(inPacket);
            //将接收到的字节数组转为对应的字符串
            msg = new String(inPacket.getData(),
                    0, inPacket.getLength(), "utf-8");
            // 发送固定格式消息回去
            remoteIP = inPacket.getAddress();
            remotePort = inPacket.getPort();
            String reply = String.format("20191002914&陈楚权& %s&%s", new Date().toString(), msg);
            send(reply);
        } catch (IOException e) {
            e.printStackTrace();
            msg = null;
        }
        return msg;
    }

    public static void main(String[] args) throws IOException {
        UDPServer udpServer = new UDPServer();
        while (true) { //等待客户端
            udpServer.receive();
        }
    }

}