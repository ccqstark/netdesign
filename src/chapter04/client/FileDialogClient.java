package chapter04.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileDialogClient {

    private Socket socket; // 定义套接字
    // 定义字符输入流和输出流
    private PrintWriter pw;
    private BufferedReader br;

    private String ip;
    private String port;

    public FileDialogClient(String ip, String port) throws IOException {
        // 主动向服务器发起连接，实现TCP的三次握手过程
        // 如果不成功，则抛出错误信息，其错误信息交由调用者处理
        socket = new Socket(ip, Integer.parseInt(port));

        // 得到网络输出字节流地址，并封装成网络输出字符流
        OutputStream socketOut = socket.getOutputStream();
        pw = new PrintWriter(new OutputStreamWriter(socketOut, StandardCharsets.UTF_8), true);

        InputStream socketIn = socket.getInputStream();
        br = new BufferedReader(new InputStreamReader(socketIn, StandardCharsets.UTF_8));
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
