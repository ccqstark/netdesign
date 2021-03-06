package chapter04.server;

import chapter03.TCPServer;
import chapter04.client.FileDialogClient;

import java.io.*;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

public class FileDialogServer {

    private int port = 2021; // 服务端监听端口
    private ServerSocket serverSocket; // 定义服务端套接字

    public FileDialogServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务端启动监听在" + port + "端口");
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        // 获得输出流缓冲区
        OutputStream socketOut = socket.getOutputStream();
        // 网络流写出需要flush，这里在PrintWriter构造方法中直接设置为自动flush
        return new PrintWriter(
                new OutputStreamWriter(socketOut, "utf-8"), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        // 获取输入流缓冲区的地址
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    public void fileListPushToClient(PrintWriter pw) {
        String path = "/Users/ccqstark/Data/Course/大三上/互联网程序设计/第四讲/server_ftp"; //给出下载目录路径
        File filePath = new File(path);
        if (!filePath.exists()) { //路径不存在则返回
            System.out.println("ftp下载目录不存在");
            return;
        }
        //如果不是一个目录就返回
        if (!filePath.isDirectory()) {
            System.out.println("不是一个目录");
            return;
        }
        //开始显示目录下的文件，不包括子目录
        String[] fileNames = filePath.list();
        File tempFile;
        // 格式化文件大小输出，不保留小数，不用四舍五入，有小数位就进1
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(0);
        formater.setRoundingMode(RoundingMode.CEILING);

        for (String fileName : fileNames) {
            tempFile = new File(filePath + "/" + fileName);
            if (tempFile.isFile()) {
                pw.println("                               " +
                        fileName + "  " + formater.format(tempFile.length() / 1024.0) + "KB");
            }
        }
    }

    public void Service() {
        while (true) {
            Socket socket = null;
            try {
                // 此处程序阻塞等待，监听并等待客户端发起连接，有连接连接请求就生成一个套接字
                socket = serverSocket.accept();
                // 本地服务器控制台显示客户端连接的用户信息
                System.out.println("New connection accepted: " + socket.getInetAddress().getHostAddress());
                BufferedReader br = getReader(socket); // 定义字符串输入流
                PrintWriter pw = getWriter(socket); // 定义字符串输出流
                // 客户端正常连接成功，则发送服务器的欢迎信息，然后等待客户端发送信息
                pw.println("From 服务器：欢迎使用本服务!这是个<FTP服务器>");

                String msg = null;
                // 此处程序阻塞，每次从输入流中读入一行字符串
                while ((msg = br.readLine()) != null) {
                    // 如果客户端发送的消息为"bye"，就结束通信
                    if (msg.equals("bye")) {
                        pw.println("From 服务器: 服务器断开连接，结束服务！");
                        System.out.println("客户端离开");
                        break; // 结束循环
                    }
                    if (msg.equals("dir")) {
                        fileListPushToClient(pw);
                    } else {
                        // 判断文件是否存在
                        String filePath = "/Users/ccqstark/Data/Course/大三上/互联网程序设计/第四讲/server_ftp";
                        String fileName = msg;
                        boolean fileExist = FileDataServer.isValidFileName(fileName, filePath);
                        if (fileExist) {
                            DecimalFormat formater = new DecimalFormat();
                            formater.setMaximumFractionDigits(0);
                            formater.setRoundingMode(RoundingMode.CEILING);
                            File tempFile = new File(filePath + "/" + fileName);
                            pw.println("文件存在，可以下载，信息为：" + fileName + "  " + formater.format(tempFile.length() / 1024.0) + "KB");
                        } else {
                            pw.println("该文件不存在！");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.close(); // 关闭socket连接及相关的输入输出流
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new FileDialogServer().Service();
    }

}
