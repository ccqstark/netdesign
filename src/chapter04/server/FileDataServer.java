package chapter04.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileDataServer {

    private int port = 2020; // 服务端监听端口
    private ServerSocket serverSocket; // 定义服务端套接字
    private Socket socket;

    public FileDataServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务端启动监听在" + port + "端口");
    }

    public void downloadFile() throws IOException {
        socket = serverSocket.accept();
        try {
            InputStream socketIn = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));

            String downFileName = br.readLine();
            System.out.println("要下载的文件为：" + downFileName);

            String filePath = "/Users/ccqstark/Data/Course/大三上/互联网程序设计/第四讲/server_ftp";
            if (downFileName == null || !isValidFileName(downFileName, filePath)) {
                socket.close(); //文件名无效，关闭连接
                return;
            }
            downFileName = filePath + "/" + downFileName;
            //读取ftp服务器上的文件，写出到网络字节流
            OutputStream socketOut = socket.getOutputStream();
            FileInputStream fileIn = new FileInputStream(downFileName);
            byte[] buf = new byte[1024]; //用来缓存字节数据
            int size;
            while ((size = fileIn.read(buf)) != -1) { //读取结束返回-1
                socketOut.write(buf, 0, size);
            }
            socketOut.flush();
            socketOut.close();
            fileIn.close();

            System.out.println(downFileName + " 文件传输结束");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close(); //关闭socket及其关联的输入输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isValidFileName(String downFileName, String filePath) {
        File file = new File(filePath + "/" + downFileName);
        return file.exists();
    }

    public void Service() {

        while (true) {
            try {
                downloadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new FileDataServer().Service();
    }

}
