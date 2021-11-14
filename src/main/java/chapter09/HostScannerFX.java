package chapter09;

import chapter01.TextFileIO;
import chapter08.HTTPClient;
import chapter08.HTTPSClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class HostScannerFX extends Application {

    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("网页请求");
    private final Button btnClear = new Button("清空");
    private final Button hostScan = new Button("主机扫描");
    private final Button cmdExec = new Button("执行命令");

    // 记录连接状态
    private static boolean connectState = false;

    //显示信息的文本区域
    private final TextArea taDisplay = new TextArea();
    // IP地址输入框
    private final TextField startInput = new TextField();
    // 端口输入框
    private final TextField endInput = new TextField();
    // 命令输入框
    private final TextField cmdInput = new TextField();

    // TCP客户端
    private HTTPClient httpClient;
    private HTTPSClient httpsClient;

    // 线程
    private Thread receiveThread;
    Thread cmdThread;

    public static void main(String[] args) {
        launch(args);
    }

    public void exitSocket() throws InterruptedException {
        if (httpClient != null) {
            httpClient.send("bye");
            // 避免tcpClient关闭后，子线程还在读取导致错误
            // 方法1：主线程sleep
//            sleep(500);
            // 方法2：interrupt结束子线程
            receiveThread.interrupt();
            receiveThread.join();

            httpClient.close();
        }
        System.exit(0);
    }

    public void sendSocketMsg() {

    }

    @Override
    public void start(Stage primaryStage) {

        // 事件
        btnExit.setOnAction((event) -> {
                    try {
                        exitSocket();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
        btnSend.setOnAction((event) -> {
            sendSocketMsg();
        });
        TextFileIO textFileIO = new TextFileIO();
        btnClear.setOnAction((event) ->
                taDisplay.clear()
        );

        // 连接按钮
        hostScan.setOnAction(event -> {
            String ip = startInput.getText().trim();
            String port = endInput.getText().trim();
            try {
                httpClient = new HTTPClient(ip, port);
                startReceiveThread();
            } catch (IOException e) {
                taDisplay.appendText("服务器连接失败!" + e.getMessage() + "\n");
            }
        });

        hostScan.setOnAction(event -> {
            startScanThreads();
        });

        cmdExec.setOnAction(event -> {
            cmdExecThread();
        });

        BorderPane mainPane = new BorderPane();

        //内容显示区域
        VBox vBox = new VBox();
        vBox.setSpacing(10);//各控件之间的间隔
        //VBox面板中的内容距离四周的留空区域
        vBox.setPadding(new Insets(10, 20, 5, 20));
        vBox.getChildren().addAll(new Label("信息显示区："),
                taDisplay);
        //设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        taDisplay.setPrefSize(600, 300);
        mainPane.setTop(vBox);

        // ip和端口输入区
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(5, 15, 5, 15));
        flowPane.getChildren().addAll(new Label("起始地址:"), startInput, new Label("结束地址:"), endInput, hostScan);
        mainPane.setCenter(flowPane);

        //底部按钮区域
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5, 20, 5, 20));
        cmdInput.setPrefSize(400, 25);
        hBox.getChildren().addAll(new Label("输入命令:"), cmdInput, cmdExec);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 430);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            try {
                exitSocket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();

    }

    // 启动一个线程来接受信息，解决主线程阻塞和多行读取的问题
    public void startReceiveThread() {
        receiveThread = new Thread(() -> {
            String msg = null;
            try {
                while ((msg = httpClient.receive()) != null) {
                    // 有效final使得lambda可以访问外部的局部变量
                    String msgTemp = msg;
                    Platform.runLater(() -> {
                        taDisplay.appendText(msgTemp + "\n");
                    });
                    sleep(1);
                }
            } catch (InterruptedException e) {
                System.out.println("子线程结束");
                // 跳出循环
                Platform.runLater(() -> {
                    taDisplay.appendText("对话已关闭!\n");
                });
            }
        });
        receiveThread.start();
    }

    public void startScanThreads() {
        receiveThread = new Thread(() -> {
            try {
                String startIpStr = startInput.getText();
                String endIpStr = endInput.getText();
                long startIp = ipToLong(startIpStr);
                long endIp = ipToLong(endIpStr);
                for (long i = startIp; i <= endIp; i++) {
                    InetAddress addr = InetAddress.getByName(longToIp(i));//host为IP地址
                    boolean status = addr.isReachable(500);
                    final String nowIp = longToIp(i);
                    if (status) {
                        for (int j = 9999; j <= 10000; j++) {
                            try {
                                final int port = j;
                                System.out.println("正在扫描" + port + "端口");
                                Socket socket = new Socket();
                                socket.connect(new InetSocketAddress("202.116.195.71",port), 5000);
                                socket.close();
                                Platform.runLater(() -> {
                                    taDisplay.appendText(port + "\n");
                                });
                            } catch (Exception e) {
                                System.out.println("不行啊");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("子线程结束");
                // 跳出循环
                Platform.runLater(() -> {
                    taDisplay.appendText("对话已关闭!\n");
                });
            }
        });
        receiveThread.start();
    }

    public void cmdExecThread() {
        cmdThread = new Thread(() -> {
            try {
                String cmd = cmdInput.getText();
                Process process = Runtime.getRuntime().exec(cmd);
                InputStream in = process.getInputStream();
                //由于简体中文系统中，命令行程序输出的中文为gb2312编码，所以这里通过流接管其输出，也需要为gb2312（或gbk）编码
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "gbk"));
                String msg;
                while ((msg = br.readLine()) != null) {
                    final String msgTemp = msg;
                    Platform.runLater(()->{
                        taDisplay.appendText(msgTemp + "\n");
                    });
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
        cmdThread.start();
    }

    /**
     * 字符串格式ip转有符号长整数
     */
    public long ipToLong(String ip) {
        String[] ipArray = ip.split("\\.");
        long num = 0;
        for (int i = 0; i < ipArray.length; i++) {
            long valueOfSection = Long.parseLong(ipArray[i]);
            num = (valueOfSection << 8 * (3 - i)) | num;
        }
        return num;
    }

    /**
     * 长整型转ip
     */
    public String longToIp(long i) {
        //右移，并将高位置0
        return ((i >> 24) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                (i & 0xFF);
    }

}
