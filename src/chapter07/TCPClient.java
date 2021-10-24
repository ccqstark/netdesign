package chapter07;

import java.io.*;
import java.net.Socket;

public class TCPClient {

    private Socket socket; // 定义套接字
    // 定义字符输入流和输出流
    private  BufferedReader reader;
    private OutputStreamWriter writer;

    public TCPClient(String ip, String port) throws IOException {
        // 主动向服务器发起连接，实现TCP的三次握手过程
        // 如果不成功，则抛出错误信息，其错误信息交由调用者处理
        socket = new Socket(ip, Integer.parseInt(port));

        // 得到网络输出字节流地址，并封装成网络输出字符流
        InputStream is = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        OutputStream os = socket.getOutputStream();
        writer = new OutputStreamWriter(os, "gbk");
    }

    public void send(String msg) throws IOException {
        writer.write(msg+"\r\n");
        writer.flush();
    }

    public String receive() {
        String msg = null;
        try {
            // 从网络输入字节流中读信息，每次只能接受一行信息
            // 如果不够一行（无行结束符），则该语句阻塞等待
            // 直到条件满足，程序才往下运行
            msg = reader.readLine();
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

    public void sendMail(String host, String dest) throws Exception {

        // smtp是25端口
        Socket socket = new Socket(host, 25);

        InputStream is = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(os, "gbk");

        writer.write("HELO a\r\n");
        writer.write("auth login\r\n");
        writer.write("Y2Nxc3RhcmtAcXEuY29t\r\n");
        writer.write("cXprdG54aW5xdWZwampiZw==\r\n");
        writer.write("MAIL FROM:<ccqstark@qq.com>\r\n");
        writer.write("RCPT TO:<" + dest + ">\r\n");	// 确认接收者
        writer.write("DATA\r\n");

        // 正文
        writer.write("这是一封自动发送的邮件\r\n");

        writer.write("\r\n.\r\n");	// 正文结束
        writer.write("QUIT\r\n");
        writer.flush();

        // 读取回传的信息
//        String line;
//        while((line=reader.readLine()) != null) {
//            System.out.println(line);
//        }
    }

}
