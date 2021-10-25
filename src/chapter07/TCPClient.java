package chapter07;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

public class TCPClient {

    private Socket socket; // 定义套接字
    // 定义字符输入流和输出流
    private BufferedReader reader;
    private OutputStreamWriter writer;

    public TCPClient(String ip, String port) throws IOException {
        // 主动向服务器发起连接，实现TCP的三次握手过程
        // 如果不成功，则抛出错误信息，其错误信息交由调用者处理
        socket = new Socket(ip, Integer.parseInt(port));

        // 得到网络输出字节流地址，并封装成网络输出字符流
        InputStream is = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        OutputStream os = socket.getOutputStream();
        writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
    }

    public void send(String msg) throws IOException, InterruptedException {
        writer.write(msg);
        writer.flush();
        sleep(100);
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

    public void sendMail(String host, String dest, String subject, String emailText) throws Exception {

        this.send("HELO a\r\n");
        this.send("auth login\r\n");
        this.send("ODQ0MzA5MDYxQHFxLmNvbQ==\r\n");
        this.send("enVmZm5nenJza29yYmViYg==\r\n");
        this.send("MAIL FROM:<844309061@qq.com>\r\n");
        this.send("RCPT TO:<" + dest + ">\r\n");    // 确认接收者
        this.send("DATA\r\n");

        this.send("From:" + "844309061@qq.com" + "\r\n");
        this.send("Subject:" + subject + "\r\n");
        this.send("To:" + dest + "\r\n");

        this.send("\r\n");

        // 正文
        this.send(emailText + "\r\n");

        this.send(".\r\n");    // 正文结束
        this.send("QUIT\r\n");

    }

}
