package chapter08;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class HTTPSClient {

    //定义SSL套接字
    private SSLSocket socket;
    //定义SSL工厂类
    private SSLSocketFactory factory;
    // 定义字符输入流和输出流
    private PrintWriter pw;
    private BufferedReader br;

    public HTTPSClient(String ip, String port) throws IOException {
        // 主动向服务器发起连接，实现TCP的三次握手过程
        // 如果不成功，则抛出错误信息，其错误信息交由调用者处理
        factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        //创建安全套接字实例
        socket = (SSLSocket) factory.createSocket(ip, Integer.parseInt(port));

        // 得到网络输出字节流地址，并封装成网络输出字符流
        OutputStream socketOut = socket.getOutputStream();
        pw = new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);

        InputStream socketIn = socket.getInputStream();
        br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    public void send(String msg) {
        pw.println(msg);
    }

    public String receive() {
        String msg = null;
        try {
            // 从网络输入字节流中读信息，每次只能接受一行信息
            // 如果不够一行（无行结束符），则该语句阻塞等待
            // 直到条件满足，程序才往下运行
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void close() {
        try {
            if (socket != null) {
                // 关闭socket连接及相关的输入流输出流，实现四次握手断开
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
